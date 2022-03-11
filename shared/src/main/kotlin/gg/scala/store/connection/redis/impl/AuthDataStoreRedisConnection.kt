package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class AuthDataStoreRedisConnection(
    private val details: DataStoreRedisConnectionDetails
) : AbstractDataStoreRedisConnection()
{
    override fun createNewConnection(): RedisClient
    {
        return RedisClient.create(
            RedisURI.builder()
                .withDatabase(0)
                .withPassword(details.password!!.toCharArray())
                .withHost(details.hostname)
                .withPort(details.port)
                .build()
        )
    }
}
