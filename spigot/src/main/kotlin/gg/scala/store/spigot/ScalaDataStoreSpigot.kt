package gg.scala.store.spigot

import com.google.gson.Gson
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import gg.scala.aware.AwareHub
import gg.scala.aware.uri.WrappedAwareUri
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
class ScalaDataStoreSpigot : JavaPlugin()
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaDataStoreSpigot>()
    }

    var mongo by Delegates.notNull<DataStoreMongoConnectionDetails>()
    var redis by Delegates.notNull<DataStoreRedisConnectionDetails>()

    var settings by Delegates.notNull<ScalaDataStoreSpigotSettings>()

    var configApi: ConfigFactory = ConfigFactory.newFactory(this)
    var client: MongoClient? = null

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

        createMongoClient()
        configureAwareHub()
    }

    private fun configureAwareHub()
    {
        val wrappedURI = WrappedAwareUri(
            address = redis.hostname,
            port = redis.port,
            password = redis.password,
            index = redis.database ?: 0
        )

        val defaultGsonInstance = Gson()
        if (redis.pubSubPublishTarget == "__default__")
        {
            AwareHub.configure(wrappedURI) { defaultGsonInstance }
        } else
        {
            AwareHub.configure(wrappedURI, redis.pubSubPublishTarget) { defaultGsonInstance }
        }
    }

    fun createMongoClient(): MongoClient
    {
        if (client == null)
        {
            client = MongoClient(
                MongoClientURI(mongo.uri)
            )
        }

        return client!!
    }

    override fun onDisable()
    {
        client?.close()
        DataStoreObjectControllerCache.closeAll()
    }
}
