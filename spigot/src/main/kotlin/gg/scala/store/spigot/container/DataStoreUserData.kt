package gg.scala.store.spigot.container

import gg.scala.store.storage.storable.AbstractStorableObject
import java.util.*

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class DataStoreUserData(
    @JvmField
    @Transient
    val uniqueId: UUID,
    var username: String
) : AbstractStorableObject()
{
    override val identifier: UUID = uniqueId
}
