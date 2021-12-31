package gg.scala.store.velocity

import gg.scala.store.velocity.settings.VelocitySettingsModel
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 12/31/2021
 */
data class ScalaDataStoreVelocitySettings
@JvmOverloads
constructor(
    val debug: Boolean = false
) : VelocitySettingsModel()
{
    override fun getAbstractType() = ScalaDataStoreVelocitySettings::class.java
}
