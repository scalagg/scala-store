package gg.scala.store.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.debug
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
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
    private val localCache = ConcurrentHashMap<UUID, D>()
    val localLayerCache = mutableMapOf<DataStoreStorageType, AbstractDataStoreStorageLayer<*, D>>()

    var serializer: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls().create()

    operator fun get(uniqueId: UUID) = localCache[uniqueId]

    fun provideCustomSerializer(gson: Gson)
    {
        serializer = gson
    }

    fun preLoadResources()
    {
        localLayerCache[DataStoreStorageType.MONGO] = MongoDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewMongoConnection(), this, dataType
        )

        localLayerCache[DataStoreStorageType.REDIS] = RedisDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getNewRedisConnection(), this, dataType
        )
    }

    inline fun <reified T : AbstractDataStoreStorageLayer<*, D>, R> useLayerWithReturn(
        type: DataStoreStorageType, lambda: T.() -> R
    ): R
    {
        type.validate()

        val layer = localLayerCache[type]
            ?: throw RuntimeException("No layer found with ${type.name} (is it queryable?)")

        return (layer as T).let(lambda)
    }

    inline fun <reified T : AbstractDataStoreStorageLayer<*, D>> useLayer(
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
        val start = System.currentTimeMillis()
        "Loading $identifier...".debug(javaClass.simpleName)

        return load(identifier, type).thenApply {
            if (it == null)
                "Couldn't find $identifier's data in ${type.name}".debug(javaClass.simpleName)
            else
                "Found $identifier's data in ${type.name}".debug(javaClass.simpleName)

            val data = it ?: ifAbsent.invoke()
            localCache[identifier] = data

            "Completed caching in ${System.currentTimeMillis() - start}ms".debug(javaClass.simpleName)

            return@thenApply data
        }
    }

    fun remove(identifier: UUID): D?
    {
        return localCache.remove(identifier)
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
}
