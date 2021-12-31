package gg.scala.store.storage.type

import gg.scala.store.storage.storable.AbstractDataStoreObject

/**
 * Represents the layer in which the
 * [AbstractDataStoreObject] will be pushed to.
 *
 * [ALL] - Pushes to both [MONGO] & [REDIS]
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
enum class DataStoreStorageType(
    private val queryable: Boolean = true
)
{
    MONGO, REDIS,
    CACHE(false),
    ALL(false);

    fun validate()
    {
        if (!queryable)
            throw RuntimeException("Cannot use a non-queryable type")
    }
}
