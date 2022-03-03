package gg.scala.store.storage.impl

import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import org.bson.conversions.Bson
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class RedisDataStoreStorageLayer<D : IDataStoreObject>(
    connection: AbstractDataStoreRedisConnection,
    private val container: DataStoreObjectController<D>,
    private val dataType: KClass<D>
) : AbstractDataStoreStorageLayer<AbstractDataStoreRedisConnection, D, (D) -> Boolean>(connection)
{
    private var section by Delegates.notNull<String>()

    /**
     * Represents the "directory" in which all
     * the objects will be stored in.
     *
     * Similar to how [AbstractDataStoreMongoConnection]
     * handles its collections, we're using the [dataType]
     * simpleName as the subdirectory to DataStore's
     * parent directory.
     */
    init
    {
        withCustomSection {
            append(dataType.simpleName)
        }
    }

    /**
     * Allow a user to build their own
     * custom section with our Redis cache.
     *
     * All sections must start with `DataStore:`
     */
    fun withCustomSection(
        section: StringBuilder.() -> Unit
    )
    {
        val builder = StringBuilder()
            .append("DataStore:")

        this.section = section
            .invoke(builder).toString()
    }

    override fun loadAllWithFilterSync(
        filter: (D) -> Boolean
    ): Map<UUID, D>
    {
        return loadAllSync().filter {
            filter.invoke(it.value)
        }
    }

    override fun loadWithFilterSync(filter: (D) -> Boolean): D?
    {
        return loadAllSync().filter {
            filter.invoke(it.value)
        }.values.firstOrNull()
    }

    override fun saveSync(data: D)
    {
        runSafely {
            connection.useResource {
                hset(
                    section, data.identifier.toString(),
                    container.serializer.serialize(data)
                )
            }
        }
    }

    override fun loadSync(identifier: UUID): D?
    {
        return runSafelyReturn {
            val serialized = connection.useResourceWithReturn {
                hget(section, identifier.toString())
            } ?: return@runSafelyReturn null

            return@runSafelyReturn container.serializer
                .deserialize(dataType, serialized)
        }
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        return runSafelyReturn {
            val serialized = connection.useResourceWithReturn {
                hgetAll(section)
            } ?: return@runSafelyReturn mutableMapOf()

            val deserialized = mutableMapOf<UUID, D>()

            for (mutableEntry in serialized)
            {
                deserialized[UUID.fromString(mutableEntry.key)] = container.serializer
                    .deserialize(dataType, mutableEntry.value)
            }

            return@runSafelyReturn deserialized
        }
    }

    override fun deleteSync(identifier: UUID)
    {
        runSafely {
            connection.useResource {
                hdel(section, identifier.toString())
            }
        }
    }

}
