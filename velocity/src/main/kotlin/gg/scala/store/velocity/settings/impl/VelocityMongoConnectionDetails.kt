package gg.scala.store.velocity.settings.impl

import gg.scala.store.velocity.settings.VelocitySettingsModel

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class VelocityMongoConnectionDetails
@JvmOverloads
constructor(
    val uri: String = "mongodb://127.0.0.1:27017/admin",
    var database: String = "Scala"
) : VelocitySettingsModel()
{
    override fun getAbstractType() = VelocityMongoConnectionDetails::class.java
}

