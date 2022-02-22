package gg.scala.store.bungee.settings.impl

import gg.scala.store.bungee.settings.BungeeSettingsModel

/**
 * @author Foraged
 * @since 22/02/2022
 */
data class BungeeRedisConnectionDetails
@JvmOverloads
constructor(
    val hostname: String = "127.0.0.1",
    val port: Int = 6379,
    val password: String? = null
) : BungeeSettingsModel()
{
    override fun getAbstractType() = BungeeRedisConnectionDetails::class.java
}
