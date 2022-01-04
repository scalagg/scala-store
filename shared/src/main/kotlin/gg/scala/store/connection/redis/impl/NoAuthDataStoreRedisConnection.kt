package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class NoAuthDataStoreRedisConnection(
    private val details: DataStoreRedisConnectionDetails
) : AbstractDataStoreRedisConnection()
{
    override fun getAppliedResource(): Jedis
    {
        return try
        {
            handle.resource
        } catch (exception: Exception)
        {
            val connection = createNewConnection()
            setConnection(connection)

            connection.resource
        }
    }

    override fun createNewConnection(): JedisPool
    {
        return JedisPool(details.hostname, details.port)
    }
}
