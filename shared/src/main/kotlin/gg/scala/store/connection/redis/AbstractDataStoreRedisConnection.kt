package gg.scala.store.connection.redis

import gg.scala.store.connection.AbstractDataStoreConnection
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.io.IOException
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreRedisConnection : AbstractDataStoreConnection<JedisPool, Jedis>()
{
    internal var handle by Delegates.notNull<JedisPool>()

    abstract fun getAppliedResource(): Jedis

    override fun getConnection() = handle

    override fun useResource(lambda: Jedis.() -> Unit)
    {
        val applied = getAppliedResource()
        lambda.invoke(applied)

        applied.close()
    }

    override fun <T> useResourceWithReturn(
        lambda: Jedis.() -> T
    ): T
    {
        val applied = getAppliedResource()

        val resource = lambda.invoke(applied)
        applied.close()

        return resource
    }

    /**
     * The current [JedisPool] should be closed before
     * we replace it with the new connection.
     *
     * New connections should not be created if the
     * current connection was successful.
     */
    override fun setConnection(connection: JedisPool)
    {
        try
        {
            close()
        } catch (exception: Exception)
        {
            LOGGER.logSevereException(exception)
        }

        handle = connection
    }

    override fun close()
    {
        try
        {
            handle.close()
        } catch (exception: Exception)
        {
            throw IOException(exception.message)
        }
    }
}
