package gg.scala.store.bungee.settings.impl

import gg.scala.store.bungee.settings.BungeeSettingsModel

/**
 * @author Foraged
 * @since 22/02/2022
 */
data class BungeeMongoConnectionDetails
@JvmOverloads
constructor(
    val uri: String = "mongodb://127.0.0.1:27017/admin",
    var database: String = "Scala"
) : BungeeSettingsModel()
{
    override fun getAbstractType() = BungeeMongoConnectionDetails::class.java
}

