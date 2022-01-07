package gg.scala.store.controller

import com.google.gson.Gson
import gg.scala.store.storage.storable.IDataStoreObject
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object DataStoreObjectControllerCache
{
    val containers = mutableMapOf<KClass<out IDataStoreObject>, DataStoreObjectController<*>>()

    fun closeAll()
    {
        containers.forEach {
            close(it.value)
        }
    }

    fun close(container: DataStoreObjectController<*>)
    {
        container.localLayerCache.forEach {
            it.value.runSafely {
                it.value.connection.close()
            }
        }
    }

    inline fun <reified T : IDataStoreObject> findNotNull(): DataStoreObjectController<T> = find()!!

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IDataStoreObject> find(): DataStoreObjectController<T>?
    {
        val container = containers[T::class]
            ?: return null

        return container as DataStoreObjectController<T>
    }

    inline fun <reified T : IDataStoreObject> create(
        serializer: Gson? = null
    ): DataStoreObjectController<T>
    {
        val container = DataStoreObjectController(T::class)
        container.preLoadResources()

        serializer?.let {
            container.provideCustomSerializer(it)
        }

        containers[T::class] = container

        return container
    }
}
