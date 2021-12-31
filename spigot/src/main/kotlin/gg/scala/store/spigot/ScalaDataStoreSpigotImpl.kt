package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.impl.NoAuthDataStoreRedisConnection
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object ScalaDataStoreSpigotImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection() = NoAuthDataStoreRedisConnection(
        DataStoreRedisConnectionDetails(
            hostname = "127.0.0.1", port = 6379
        )
    )

    override fun getNewMongoConnection() = UriDataStoreMongoConnection(
        DataStoreMongoConnectionDetails.of("mongodb://127.0.0.1:27017/admin")
    )

    override fun debug(from: String, message: String)
    {
        Bukkit.getOnlinePlayers()
            .filter { it.isOp }
            .forEach {
                it.sendMessage("[$from] [Debug] $message")
            }
    }
}
