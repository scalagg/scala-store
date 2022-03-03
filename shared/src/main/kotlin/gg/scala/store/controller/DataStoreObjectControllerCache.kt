package gg.scala.store.controller

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

    private fun close(container: DataStoreObjectController<*>)
    {
        container.localLayerCache.forEach {
            it.value.runSafely(
                printTrace = false
            ) {
                it.value.connection.close()
            }
        }
    }

    inline fun <reified T : IDataStoreObject> findNotNull():
            DataStoreObjectController<T> = find()!!

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IDataStoreObject> find():
            DataStoreObjectController<T>?
    {
        val container = containers[T::class]
            ?: return null

        return container as DataStoreObjectController<T>
    }

    inline fun <reified T : IDataStoreObject> create():
            DataStoreObjectController<T>
    {
        return create(T::class)
    }

    fun <T : IDataStoreObject> create(
        kClass: KClass<T>
    ): DataStoreObjectController<T>
    {
        val container = DataStoreObjectController(kClass)
        container.preLoadResources()

        containers[kClass] = container

        return container
    }
}
