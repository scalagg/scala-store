package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

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
        val resource = handle.resource
        resource.auth(details.password!!)

        return resource
    }

    override fun createNewConnection(): JedisPool
    {
        return JedisPool(details.hostname, details.port)
    }
}
