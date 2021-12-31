package gg.scala.store.storage

import gg.scala.store.connection.AbstractDataStoreConnection
import gg.scala.store.storage.storable.AbstractStorableObject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreStorageLayer<C : AbstractDataStoreConnection<*, *>, D : AbstractStorableObject>(
    internal val connection: C
)
{
    abstract fun saveSync(data: D)

    abstract fun loadSync(identifier: UUID): D?
    abstract fun loadAllSync(): Map<UUID, D>

    abstract fun deleteSync(identifier: UUID)

    fun save(data: D): CompletableFuture<Void>
    {
        return CompletableFuture.runAsync { saveSync(data) }
    }

    fun load(identifier: UUID): CompletableFuture<D?>
    {
        return CompletableFuture.supplyAsync { loadSync(identifier) }
    }

    fun delete(identifier: UUID): CompletableFuture<Void>
    {
        return CompletableFuture.runAsync() { deleteSync(identifier) }
    }

    fun loadAll(): CompletableFuture<Map<UUID, D>>
    {
        return CompletableFuture.supplyAsync { loadAllSync() }
    }
}
