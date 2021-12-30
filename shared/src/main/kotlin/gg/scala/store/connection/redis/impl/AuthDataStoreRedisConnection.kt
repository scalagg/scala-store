package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisException
import java.io.IOException

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class AuthDataStoreRedisConnection(
    private val details: DataStoreRedisConnectionDetails
) : AbstractDataStoreRedisConnection()
{
    override fun getAppliedResource(): Jedis
    {
        return try
        {
            val resource = handle.resource
            resource.auth(details.password!!)

            resource
        } catch (exception: JedisException)
        {
            throw IOException("Failed to use resource")
        }
    }

    override fun createNewConnection(): JedisPool
    {
        return JedisPool(details.hostname, details.port)
    }
}
