package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreSpigotImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return DataStoreRedisConnection()
    }

    override fun getNewMongoConnection(): AbstractDataStoreMongoConnection
    {
        return UriDataStoreMongoConnection(
            ScalaDataStoreSpigot.INSTANCE.mongo
        )
    }

    override fun debug(from: String, message: String)
    {
        if (!ScalaDataStoreSpigot.INSTANCE.settings.debug)
            return

        Bukkit.broadcast(
            "${ChatColor.GREEN}[$from] ${ChatColor.GRAY}[debug]: $message",
            "ds.admin"
        )
    }
}
