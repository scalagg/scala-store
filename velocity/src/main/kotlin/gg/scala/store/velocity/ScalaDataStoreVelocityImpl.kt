package gg.scala.store.velocity

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.AuthDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.NoAuthDataStoreRedisConnection
import gg.scala.store.velocity.settings.VelocitySettingsProcessor

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreVelocityImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        val details = ScalaDataStoreVelocity.INSTANCE.redis

        return if (details.password.isNullOrEmpty())
        {
            NoAuthDataStoreRedisConnection(details)
        } else
        {
            AuthDataStoreRedisConnection(details)
        }
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
