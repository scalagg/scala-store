package gg.scala.store.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.impl.details.DataStoreRedisConnectionDetails
import gg.scala.store.velocity.settings.VelocitySettingsProcessor
import gg.scala.store.velocity.settings.impl.VelocityMongoConnectionDetails
import gg.scala.store.velocity.settings.impl.VelocityRedisConnectionDetails
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/31/2021
 */
class ScalaDataStoreVelocity
@Inject
constructor(
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory
    private val directory: Path
)
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaDataStoreVelocity>()
    }

    var mongo by Delegates.notNull<DataStoreMongoConnectionDetails>()
    var redis by Delegates.notNull<DataStoreRedisConnectionDetails>()

    val directoryFile: File = directory.toFile().also {
        if (!it.exists())
        {
            it.mkdirs()
        }
    }

    init
    {
        INSTANCE = this
    }

    @Subscribe
    fun onProxyInit(event: ProxyInitializeEvent)
    {
        VelocitySettingsProcessor
            .loadSettings<ScalaDataStoreVelocitySettings>("settings")

        VelocitySettingsProcessor
            .loadSettings<VelocityRedisConnectionDetails>("redis")
        VelocitySettingsProcessor
            .loadSettings<VelocityMongoConnectionDetails>("mongo")

        // conversion of types
        VelocitySettingsProcessor.locate<VelocityRedisConnectionDetails>()
            ?.let {
                redis = DataStoreRedisConnectionDetails(
                    it.hostname, it.port, it.password
                )
            }

        VelocitySettingsProcessor.locate<VelocityMongoConnectionDetails>()
            ?.let {
                mongo = DataStoreMongoConnectionDetails(
                    it.database, it.uri
                )
            }

        ScalaDataStoreShared.INSTANCE = ScalaDataStoreVelocityImpl
    }
}
