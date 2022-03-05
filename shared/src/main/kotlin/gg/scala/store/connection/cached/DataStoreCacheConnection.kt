package gg.scala.store.connection.cached

import gg.scala.store.connection.AbstractDataStoreConnection
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 3/3/2022
 */
class DataStoreCacheConnection<K, V>(
    val handle: ConcurrentHashMap<K, V> = ConcurrentHashMap<K, V>()
) : AbstractDataStoreConnection<ConcurrentHashMap<K, V>, ConcurrentHashMap<K, V>>()
{
    override fun useResource(
        lambda: ConcurrentHashMap<K, V>.() -> Unit
    )
    {
        return lambda.invoke(handle)
    }

    override fun <T> useResourceWithReturn(
        lambda: ConcurrentHashMap<K, V>.() -> T
    ): T?
    {
        return lambda.invoke(handle)
    }

    override fun getConnection(): ConcurrentHashMap<K, V>
    {
        return handle
    }

    override fun setConnection(connection: ConcurrentHashMap<K, V>)
    {
    }

    override fun createNewConnection(): ConcurrentHashMap<K, V>
    {
        val newMap = ConcurrentHashMap<K, V>()
        close(); setConnection(newMap)

        return newMap
    }

    override fun close()
    {
        handle.clear()
    }
}
