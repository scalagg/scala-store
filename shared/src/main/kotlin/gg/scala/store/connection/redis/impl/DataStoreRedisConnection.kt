package gg.scala.store.connection.redis.impl

import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI

/**
 * @author GrowlyX
 * @since 4/14/2022
 */
class DataStoreRedisConnection(
    private val redisURI: RedisURI
) : AbstractDataStoreRedisConnection()
{
    override fun createNewConnection(): RedisClient =
        RedisClient.create(redisURI)
}
