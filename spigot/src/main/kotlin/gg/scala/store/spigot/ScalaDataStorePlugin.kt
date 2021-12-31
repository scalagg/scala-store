package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.controller.DataStoreObjectControllerCache
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class ScalaDataStorePlugin : JavaPlugin()
{
    override fun onEnable()
    {
        ScalaDataStoreShared.INSTANCE = ScalaDataStoreSpigotImpl

        ScalaDataStoreSpigotImpl.getNewMongoConnection()
            .setConnection(
                ScalaDataStoreSpigotImpl.getNewMongoConnection()
                    .createNewConnection()
            )

        ScalaDataStoreSpigotImpl.getNewRedisConnection()
            .setConnection(
                ScalaDataStoreSpigotImpl.getNewRedisConnection()
                    .createNewConnection()
            )
    }

    override fun onDisable()
    {
        DataStoreObjectControllerCache.closeAll()
    }
}
