package gg.scala.store.storage.type

import gg.scala.store.storage.storable.IDataStoreObject

/**
 * Represents the layer in which the
 * [IDataStoreObject] will be pushed to.
 *
 * [ALL] - Pushes to both [MONGO] & [REDIS]
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
enum class DataStoreStorageType(
    private val queryable: Boolean = true,
    private val queryableExtensively: Boolean = false
)
{
    MONGO(
        queryableExtensively = true
    ),
    REDIS,
    CACHE,
    ALL(
        queryable = false
    );

    fun validateExtensive()
    {
        if (!queryableExtensively)
        {
            throw IllegalStateException("Cannot use a basic query type")
        }
    }

    fun validate()
    {
        if (!queryable)
        {
            throw IllegalStateException("Cannot use a non-queryable type")
        }
    }
}
