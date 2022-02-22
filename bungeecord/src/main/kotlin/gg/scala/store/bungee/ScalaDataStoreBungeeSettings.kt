package gg.scala.store.bungee

import gg.scala.store.bungee.settings.BungeeSettingsModel

/**
 * @author Foraged
 * @since 22/02/2022
 */
data class ScalaDataStoreBungeeSettings
@JvmOverloads
constructor(
    val debug: Boolean = false
) : BungeeSettingsModel()
{
    override fun getAbstractType() = ScalaDataStoreBungeeSettings::class.java
}
