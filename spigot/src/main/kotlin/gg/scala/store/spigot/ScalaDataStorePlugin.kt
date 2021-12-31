package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.spigot.container.DataStoreUserData
import gg.scala.store.spigot.container.DataStoreUserDataContainer
import gg.scala.store.storage.type.DataStoreStorageType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class ScalaDataStorePlugin : JavaPlugin(), Listener
{
    override fun onEnable()
    {
        ScalaDataStoreShared.INSTANCE = ScalaDataStoreSpigotImpl
        DataStoreUserDataContainer.preLoadResources()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent)
    {
        DataStoreUserDataContainer.loadAndCache(event.player.uniqueId, {
            DataStoreUserData(event.player.uniqueId, event.player.name)
        }, DataStoreStorageType.MONGO)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent)
    {
        ScalaDataStoreSpigotImpl.debug("PlayerQuitEvent", "Removing & saving ${event.player.uniqueId}")

        DataStoreUserDataContainer.remove(event.player.uniqueId)?.let {
            DataStoreUserDataContainer.save(it, DataStoreStorageType.ALL)
        }

        ScalaDataStoreSpigotImpl.debug("PlayerQuitEvent", "Removed & saved ${event.player.uniqueId}")
    }
}
