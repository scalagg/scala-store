package gg.scala.store.storage.type

import gg.scala.store.storage.storable.AbstractStorableObject

/**
 * Represents the layer in which the
 * [AbstractStorableObject] will be pushed to.
 *
 * [ALL] - Pushes to both [MONGO] & [REDIS]
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
enum class DataStoreStorageType(
    val actionable: Boolean = true
)
{
    MONGO, REDIS,
    CACHE(false),
    ALL(false)
}
