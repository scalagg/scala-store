package gg.scala.store.bungee

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.AuthDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.NoAuthDataStoreRedisConnection
import gg.scala.store.bungee.settings.BungeeSettingsProcessor

/**
 * @author Foraged
 * @since 22/02/2022
 */
object ScalaDataStoreBungeeImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        val details = ScalaDataStoreBungee.INSTANCE.redis

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
            ScalaDataStoreBungee.INSTANCE.mongo
        )
    }

    override fun debug(from: String, message: String)
    {
        val settings = BungeeSettingsProcessor
            .locate<ScalaDataStoreBungeeSettings>()!!

        if (settings.debug)
        {
            ScalaDataStoreBungee.INSTANCE
                .logger.info("[$from] [Debug] $message")
        }
    }
}
