package gg.scala.store.container

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.impl.MongoDataStoreStorageLayer
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.store.storage.storable.AbstractStorableObject
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import javax.swing.ComponentInputMap
import kotlin.reflect.KClass

/**
 * A specific [AbstractStorableObject]'s
 * storage container.
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreStorableContainer<D : AbstractStorableObject>
{
    private val localCache = ConcurrentHashMap<UUID, D>()
    private val localLayerCache = mutableMapOf<DataStoreStorageType, AbstractDataStoreStorageLayer<*, D>>()

    var serializer: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls().setLenient().create()

    fun provideCustomSerializer(gson: Gson)
    {
        serializer = gson
    }

    fun preLoadResources()
    {
        // TODO: 12/30/2021 handle layer creation automatically
        localLayerCache[DataStoreStorageType.MONGO] = MongoDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getMongoConnection(), this, getDataType()
        )

        localLayerCache[DataStoreStorageType.REDIS] = RedisDataStoreStorageLayer(
            ScalaDataStoreShared.INSTANCE.getRedisConnection(), this, getDataType()
        )
    }

    inline fun <reified T> useLayer(
        type: DataStoreStorageType, lambda: T.() -> Unit
    )
    {
        if (type == DataStoreStorageType.ALL)
            throw RuntimeException("Cannot use DataStoreStorageType#ALL")


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
        if (type == DataStoreStorageType.ALL)
            throw RuntimeException("Cannot load from DataStoreStorageType#ALL")

        val layer = localLayerCache[type]!!
        return layer.load(identifier)
    }

    abstract fun getDataType(): KClass<D>

}
