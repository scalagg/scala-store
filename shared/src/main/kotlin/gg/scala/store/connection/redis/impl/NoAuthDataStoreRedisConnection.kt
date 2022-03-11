package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class NoAuthDataStoreRedisConnection(
    private val details: DataStoreRedisConnectionDetails
) : AbstractDataStoreRedisConnection()
{
    override fun createNewConnection(): RedisClient
    {
        return RedisClient.create(
            RedisURI.create(details.hostname, details.port)
        )
    }
}
