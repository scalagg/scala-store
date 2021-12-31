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
import java.util.concurrent.ConcurrentHashMap
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
    private val localLayerCache = mutableMapOf<DataStoreStorageType, AbstractDataStoreStorageLayer<*, *>>()

    var serializer: Gson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls().setLenient().create()

    abstract fun getDataType(): KClass<D>

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

}
