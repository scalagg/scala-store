package gg.scala.store.velocity

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection
import gg.scala.store.velocity.settings.VelocitySettingsProcessor
import io.lettuce.core.RedisURI

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreVelocityImpl : ScalaDataStoreShared()
{
    private val redisUri by lazy {
        val details = ScalaDataStoreVelocity.INSTANCE.redis

        return@lazy if (details.password.isNullOrEmpty())
        {
            RedisURI.create(details.hostname, details.port)
        } else
        {
            RedisURI.builder()
                .withDatabase(0)
                .withPassword(details.password!!.toCharArray())
                .withHost(details.hostname)
                .withPort(details.port)
                .build()
        }
    }

    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return DataStoreRedisConnection()
    }

    override fun getNewMongoConnection(): AbstractDataStoreMongoConnection
    {
        return UriDataStoreMongoConnection(
            ScalaDataStoreVelocity.INSTANCE.mongo
        )
    }

    override fun debug(from: String, message: String)
    {
        val settings = VelocitySettingsProcessor
            .locate<ScalaDataStoreVelocitySettings>()!!

        if (settings.debug)
        {
            ScalaDataStoreVelocity.INSTANCE
                .logger.info("[$from] [Debug] $message")
        }
    }
}
