package gg.scala.store.storage

import gg.scala.store.connection.AbstractDataStoreConnection
import gg.scala.store.storage.storable.IDataStoreObject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreStorageLayer<C : AbstractDataStoreConnection<*, *>, D : IDataStoreObject, F>(
    val connection: C
)
{
    abstract fun saveSync(data: D)

    abstract fun loadSync(identifier: UUID): D?
    abstract fun loadWithFilterSync(filter: F): D?

    abstract fun loadAllSync(): Map<UUID, D>
    abstract fun loadAllWithFilterSync(filter: F): Map<UUID, D>

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

    fun loadAllWithFilter(filter: F): CompletableFuture<Map<UUID, D>>
    {
        return CompletableFuture.supplyAsync { loadAllWithFilterSync(filter) }
    }

    fun loadWithFilter(filter: F): CompletableFuture<D?>
    {
        return CompletableFuture.supplyAsync { loadWithFilterSync(filter) }
    }

    fun saveMultiple(vararg data: D): CompletableFuture<Void>
    {
        return CompletableFuture.runAsync { saveMultipleSync(*data) }
    }

    fun deleteMultiple(vararg identifiers: UUID): CompletableFuture<Void>
    {
        return CompletableFuture.runAsync { deleteMultipleSync(*identifiers) }
    }

    fun loadMultiple(vararg identifiers: UUID): CompletableFuture<Map<UUID, D?>>
    {
        return CompletableFuture.supplyAsync { loadMultipleSync(*identifiers) }
    }

    fun saveMultipleSync(vararg data: D)
    {
        for (instance in data)
        {
            runSafely { saveSync(instance) }
        }
    }

    fun loadMultipleSync(
        vararg identifiers: UUID
    ): Map<UUID, D?>
    {
        val mutableMap = mutableMapOf<UUID, D?>()

        for (identifier in identifiers)
        {
            mutableMap[identifier] =
                runSafelyReturn {
                    loadSync(identifier)
                }
        }

        return mutableMap
    }

    fun deleteMultipleSync(
        vararg identifiers: UUID
    )
    {
        for (identifier in identifiers)
        {
            runSafely { deleteSync(identifier) }
        }
    }

    fun <T> runSafelyReturn(
        lambda: () -> T
    ): T
    {
        try
        {
            kotlin.run {
                return lambda.invoke()
            }
        } catch (exception: Exception)
        {
            exception.printStackTrace()
            throw RuntimeException("Uncaught exception in CompletableFuture chain")
        }
    }

    @JvmOverloads
    fun runSafely(
        printTrace: Boolean = true,
        lambda: () -> Unit
    )
    {
        try
        {
            kotlin.run {
                lambda.invoke()
            }
        } catch (exception: Exception)
        {
            if (printTrace)
                exception.printStackTrace()

            throw RuntimeException("Uncaught exception in CompletableFuture chain")
        }
    }

}
