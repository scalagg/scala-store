package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection
import io.lettuce.core.RedisURI
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreSpigotImpl : ScalaDataStoreShared()
{
    private val redisUri by lazy {
        val details = ScalaDataStoreSpigot.INSTANCE.redis

        return@lazy if (details.password.isNullOrEmpty())
        {
            RedisURI.create(details.hostname, details.port)
        } else
        {
            RedisURI().apply {
                this.password = details
                    .password!!.toCharArray()

                this.host = details.hostname
                this.port = details.port
            }
        }
    }

    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return DataStoreRedisConnection(redisUri)
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
