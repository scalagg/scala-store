package gg.scala.store.bungee

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.bungee.settings.BungeeSettingsProcessor
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection

/**
 * @author Foraged
 * @since 22/02/2022
 */
object ScalaDataStoreBungeeImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return DataStoreRedisConnection()
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
