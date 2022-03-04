package gg.scala.store.storage.impl

import gg.scala.store.connection.cached.DataStoreCacheConnection
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*

/**
 * @author GrowlyX
 * @since 3/3/2022
 */
class CachedDataStoreStorageLayer<D : IDataStoreObject> :
    AbstractDataStoreStorageLayer<DataStoreCacheConnection<UUID, D>, D, (D) -> Boolean>(
        DataStoreCacheConnection()
    )
{
    override fun saveSync(data: D)
    {
        connection.useResource {
            this[data.identifier] = data
        }
    }

    override fun loadSync(identifier: UUID): D?
    {
        return connection
            .useResourceWithReturn {
                this[identifier]
            }
    }

    override fun loadWithFilterSync(filter: (D) -> Boolean): D?
    {
        return connection
            .useResourceWithReturn {
                this.values.firstOrNull(filter)
            }
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        return connection.getConnection()
    }

    override fun loadAllWithFilterSync(
        filter: (D) -> Boolean
    ): Map<UUID, D>
    {
        return connection.getConnection()
            .apply {
                val filtered = this.values
                    .filter(filter)

                mutableMapOf<UUID, D>()
                    .also { map ->
                        filtered.forEach { map[it.identifier] = it }
                    }
            }
    }

    override fun deleteSync(identifier: UUID)
    {
        connection.useResource {
            remove(identifier)
        }
    }
}
