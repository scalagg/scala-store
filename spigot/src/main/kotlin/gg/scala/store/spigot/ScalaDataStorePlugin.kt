package gg.scala.store.spigot

import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import gg.scala.store.controller.DataStoreObjectControllerCache
import org.bukkit.plugin.java.JavaPlugin
import xyz.mkotb.configapi.ConfigFactory
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class ScalaDataStorePlugin : JavaPlugin()
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaDataStorePlugin>()
    }

    var mongo by Delegates.notNull<DataStoreMongoConnectionDetails>()
    var redis by Delegates.notNull<DataStoreRedisConnectionDetails>()

    var settings by Delegates.notNull<ScalaDataStoreSpigotSettings>()

    private val configApi: ConfigFactory by lazy {
        ConfigFactory.newFactory(this)
    }

    override fun onEnable()
    {
        INSTANCE = this
        ScalaDataStoreShared.INSTANCE = ScalaDataStoreSpigotImpl

        mongo = configApi.fromFile(
            "mongo", DataStoreMongoConnectionDetails::class.java
        )

        redis = configApi.fromFile(
            "redis", DataStoreRedisConnectionDetails::class.java
        )

        settings = configApi.fromFile(
            "settings", ScalaDataStoreSpigotSettings::class.java
        )
    }

    override fun onDisable()
    {
        DataStoreObjectControllerCache.closeAll()
    }
}
