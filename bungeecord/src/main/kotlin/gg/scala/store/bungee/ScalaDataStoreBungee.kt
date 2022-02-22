package gg.scala.store.bungee

import com.google.inject.Inject
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import gg.scala.store.bungee.settings.BungeeSettingsProcessor
import gg.scala.store.bungee.settings.impl.BungeeMongoConnectionDetails
import gg.scala.store.bungee.settings.impl.BungeeRedisConnectionDetails
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import kotlin.properties.Delegates

/**
 * @author Foraged
 * @since 22/02/2022
 */
class ScalaDataStoreBungee(
    val server: ProxyServer = ProxyServer.getInstance()
) : Plugin()
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaDataStoreBungee>()
    }

    var mongo by Delegates.notNull<DataStoreMongoConnectionDetails>()
    var redis by Delegates.notNull<DataStoreRedisConnectionDetails>()

    val directoryFile: File = dataFolder.also {
        if (!it.exists())
        {
            it.mkdirs()
        }
    }

    init
    {
        INSTANCE = this
        ScalaDataStoreShared.INSTANCE = ScalaDataStoreBungeeImpl
    }

    @Override
    override fun onEnable()
    {
        BungeeSettingsProcessor.initialLoad()

        BungeeSettingsProcessor
            .loadSettings<ScalaDataStoreBungeeSettings>("settings")

        BungeeSettingsProcessor
            .loadSettings<BungeeRedisConnectionDetails>("redis")
        BungeeSettingsProcessor
            .loadSettings<BungeeMongoConnectionDetails>("mongo")

        // conversion of types
        BungeeSettingsProcessor.locate<BungeeRedisConnectionDetails>()
            ?.let {
                redis = DataStoreRedisConnectionDetails(
                    it.hostname, it.port, it.password
                )
            }

        BungeeSettingsProcessor.locate<BungeeMongoConnectionDetails>()
            ?.let {
                mongo = DataStoreMongoConnectionDetails(
                    it.uri, it.database
                )
            }
    }
}
