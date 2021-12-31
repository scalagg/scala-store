package gg.scala.store.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.debug
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.storable.AbstractDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * A specific [AbstractDataStoreObject]'s
 * storage container.
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
open class DataStoreObjectController<D : AbstractDataStoreObject>(
    private val dataType: KClass<D>
)
{
    private val localCache = ConcurrentHashMap<UUID, D>()
    internal val localLayerCache = mutableMapOf<DataStoreStorageType, AbstractDataStoreStorageLayer<*, D>>()

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

    fun useLayer(
        type: DataStoreStorageType,
        lambda: AbstractDataStoreStorageLayer<*, D>.() -> Unit
    )
    {
        type.validate()

        localLayerCache[type]?.let(lambda)
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
    )
    {
        val layer = localLayerCache[type]

        if (layer == null)
        {
            localLayerCache.values.forEach {
                it.save(data)
            }
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
}