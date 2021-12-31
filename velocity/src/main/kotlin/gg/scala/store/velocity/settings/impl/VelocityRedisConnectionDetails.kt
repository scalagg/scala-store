package gg.scala.store.velocity.settings.impl

import gg.scala.store.velocity.settings.VelocitySettingsModel
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class VelocityRedisConnectionDetails
@JvmOverloads
constructor(
    val hostname: String = "127.0.0.1",
    val port: Int = 6379,
    val password: String? = null
) : VelocitySettingsModel()
{
    override fun getAbstractType() = VelocityRedisConnectionDetails::class.java
}
