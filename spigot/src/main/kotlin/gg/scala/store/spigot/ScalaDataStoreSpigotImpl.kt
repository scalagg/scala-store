package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.AuthDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.NoAuthDataStoreRedisConnection
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreSpigotImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        val details = ScalaDataStorePlugin.INSTANCE.redis

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
            ScalaDataStorePlugin.INSTANCE.mongo
        )
    }

    override fun debug(from: String, message: String)
    {
        if (!ScalaDataStorePlugin.INSTANCE.settings.debug)
            return

        Bukkit.getOnlinePlayers()
            .filter { it.isOp }
            .forEach {
                it.sendMessage("[$from] [Debug] $message")
            }
    }
}
