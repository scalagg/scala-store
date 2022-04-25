package gg.scala.store.controller

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.controller.annotations.Timestamp
import gg.scala.store.debug
import gg.scala.store.serializer.DataStoreSerializer
import gg.scala.store.serializer.serializers.GsonSerializer
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.impl.CachedDataStoreStorageLayer
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * A specific [IDataStoreObject]'s
 * storage container.
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
open class DataStoreObjectController<D : IDataStoreObject>(
    private val dataType: KClass<D>
)
{
    val localLayerCache = mutableMapOf<DataStoreStorageType, AbstractDataStoreStorageLayer<*, D, *>>()
    var serializer: DataStoreSerializer = GsonSerializer

    fun useSerializer(
        serializer: DataStoreSerializer
    )
    {
        this.serializer = serializer
    }

    private var timestampField: Field? = null
    private var timestampThreshold = 2000L

    fun preLoadResources()
    {
        localLayerCache[DataStoreStorageType.MONGO] = MongoDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewMongoConnection(), this, dataType
        )

        localLayerCache[DataStoreStorageType.REDIS] = RedisDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewRedisConnection(), this, dataType
        )

        this.timestampField = dataType
            .java.fields.firstOrNull {
                it.isAnnotationPresent(Timestamp::class.java)
            }

        localLayerCache[DataStoreStorageType.CACHE] = CachedDataStoreStorageLayer()
    }

    fun localCache(): ConcurrentHashMap<UUID, D>
    {
        return useLayerWithReturn<CachedDataStoreStorageLayer<D>, ConcurrentHashMap<UUID, D>>(
            DataStoreStorageType.CACHE
        ) {
            this.connection.handle
        }
    }

    inline fun <reified T : AbstractDataStoreStorageLayer<*, D, *>, U> useLayerWithReturn(
        type: DataStoreStorageType, lambda: T.() -> U
    ): U
    {
        type.validate()

        val layer = localLayerCache[type]
            ?: throw RuntimeException("No layer found with ${type.name}")

        return (layer as T).let(lambda)
    }

    inline fun <reified T : AbstractDataStoreStorageLayer<*, D, *>> useLayer(
        type: DataStoreStorageType, lambda: T.() -> Unit
    )
    {
        type.validate()

        localLayerCache[type]?.let {
            (it as T).let(lambda)
        }
    }

    fun loadOptimalCopy(
        identifier: UUID,
        ifAbsent: () -> D
    ): CompletableFuture<D>
    {
        val debugFrom = "${identifier}-${dataType.simpleName}"
        val start = System.currentTimeMillis()

        val extendedAbsent = {
            "Creating new copy".debug(debugFrom)
            ifAbsent.invoke()
        }

        val typeUsed = if (this.timestampField == null)
            DataStoreStorageType.MONGO else DataStoreStorageType.REDIS

        "Loading from ${typeUsed.name}...".debug(debugFrom)

        return load(
            identifier, typeUsed
        ).thenApply {
            if (it == null)
                "Couldn't find a copy in ${typeUsed.name}".debug(debugFrom)
            else
                "Found copy in ${typeUsed.name}".debug(debugFrom)

            if (
                this.timestampField != null &&
                it != null
            )
            {
                val timestamp = this
                    .timestampField!!
                    .get(it)

                if (timestamp != null)
                {
                    "Found timestamp from copy".debug(debugFrom)

                    val difference =
                        System.currentTimeMillis() - timestamp as Long

                    val exceedsThreshold =
                        difference >= this.timestampThreshold

                    if (exceedsThreshold)
                    {
                        "Timestamp exceeds threshold, retrieving from mongo".debug(debugFrom)

                        return@thenApply this.load(
                            identifier,
                            DataStoreStorageType.MONGO
                        ).join()
                    }
                }
            }

            val data = if (
                it == null &&
                this.timestampField != null
            )
            {
                "Attempting to retrieve from MONGO".debug(debugFrom)

                this.load(
                    identifier,
                    DataStoreStorageType.MONGO
                ).join() ?: extendedAbsent.invoke()
            } else
            {
                extendedAbsent.invoke()
            }

            useLayer<CachedDataStoreStorageLayer<D>>(
                DataStoreStorageType.CACHE
            ) {
                this.saveSync(data)
            }

            "Completed process in ${System.currentTimeMillis() - start}ms".debug(debugFrom)

            return@thenApply data
        }
    }

    fun save(
        data: D,
        type: DataStoreStorageType = DataStoreStorageType.ALL
    ): CompletableFuture<Void>
    {
        var properType = type

        // updating the last-saved timestamp
        this.timestampField?.apply {
            set(data, System.currentTimeMillis())

            // we want a copy stored in
            // both MONGO & REDIS
            properType = DataStoreStorageType.ALL
        }

        val layer = localLayerCache[properType]

        return if (layer == null)
        {
            val status = CompletableFuture<Void>()

            localLayerCache.values.forEach { storageLayer ->
                status.thenCompose {
                    storageLayer.save(data)
                }
            }

            status
        } else
        {
            layer.save(data)
        }
    }

    fun load(
        identifier: UUID,
        type: DataStoreStorageType
    ): CompletableFuture<D?>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.load(identifier)
    }

    fun delete(
        identifier: UUID,
        type: DataStoreStorageType
    ): CompletableFuture<Void>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.delete(identifier)
    }

    fun loadAll(
        type: DataStoreStorageType
    ): CompletableFuture<Map<UUID, D>>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.loadAll()
    }

    fun loadMultiple(
        type: DataStoreStorageType,
        vararg identifiers: UUID
    ): CompletableFuture<Map<UUID, D?>>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.loadMultiple(*identifiers)
    }

    fun saveMultiple(
        type: DataStoreStorageType,
        vararg objects: D
    ): CompletableFuture<Void>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.saveMultiple(*objects)
    }

    fun deleteMultiple(
        type: DataStoreStorageType,
        vararg identifiers: UUID
    ): CompletableFuture<Void>
    {
        type.validate()

        val layer = localLayerCache[type]!!
        return layer.deleteMultiple(*identifiers)
    }
}
