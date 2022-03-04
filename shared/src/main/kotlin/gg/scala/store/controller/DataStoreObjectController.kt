package gg.scala.store.controller

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.debug
import gg.scala.store.serializer.DataStoreSerializer
import gg.scala.store.serializer.serializers.GsonSerializer
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.impl.CachedDataStoreStorageLayer
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture
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

    fun preLoadResources()
    {
        localLayerCache[DataStoreStorageType.MONGO] = MongoDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewMongoConnection(), this, dataType
        )

        localLayerCache[DataStoreStorageType.REDIS] = RedisDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewRedisConnection(), this, dataType
        )

        localLayerCache[DataStoreStorageType.CACHE] = CachedDataStoreStorageLayer()
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

    fun loadAndCache(
        identifier: UUID,
        ifAbsent: () -> D,
        type: DataStoreStorageType
    ): CompletableFuture<D>
    {
        val debugFrom = "${javaClass.simpleName}_${dataType.simpleName}"

        val start = System.currentTimeMillis()
        "Loading $identifier...".debug(debugFrom)

        return load(identifier, type).thenApply {
            if (it == null)
                "Couldn't find $identifier's data in ${type.name}".debug(debugFrom)
            else
                "Found $identifier's data in ${type.name}".debug(debugFrom)

            val data = it ?: ifAbsent.invoke()

            useLayer<CachedDataStoreStorageLayer<D>>(
                DataStoreStorageType.CACHE
            ) {
                this.saveSync(data)
            }

            "Completed caching in ${System.currentTimeMillis() - start}ms".debug(debugFrom)

            return@thenApply data
        }
    }

    fun save(
        data: D,
        type: DataStoreStorageType = DataStoreStorageType.ALL
    ): CompletableFuture<Void>
    {
        val layer = localLayerCache[type]

        return if (layer == null)
        {
            var status = CompletableFuture<Void>()

            localLayerCache.values.forEach {
                status = it.save(data)
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
