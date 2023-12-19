package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.redis.impl.DataStoreRedisConnection
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreSpigotImpl : ScalaDataStoreShared()
{
    private val redisConnection by lazy {
        DataStoreRedisConnection()
    }

    private val mongoConnection by lazy {
        UriDataStoreMongoConnection(
            ScalaDataStoreSpigot.INSTANCE.mongo,
            ScalaDataStoreSpigot.INSTANCE.createMongoClient()
        )
    }

    override fun forceDisableRedisThreshold() = true

    override fun getNewRedisConnection() = redisConnection
    override fun getNewMongoConnection() = mongoConnection

    override fun debug(from: String, message: String)
    {
        if (!ScalaDataStoreSpigot.INSTANCE.settings.debug)
            return

        Bukkit.broadcast(
            "[$from] [debug] $message",
            "ds.admin"
        )

        Bukkit.getPluginManager()
            .getPlugin("store-spigot")
            .logger.info("[$from] [debug] $message")
    }
}
