package gg.scala.store.storage.impl

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.storable.IDataStoreObject
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class MongoDataStoreStorageLayer<D : IDataStoreObject>(
    connection: AbstractDataStoreMongoConnection,
    private val container: DataStoreObjectController<D>,
    private val dataType: KClass<D>
) : AbstractDataStoreStorageLayer<AbstractDataStoreMongoConnection, D, Bson>(connection)
{
    private var collection by Delegates.notNull<MongoCollection<Document>>()
    private val upsetOptions = UpdateOptions().upsert(true)

    init
    {
        collection = connection
            .useResourceWithReturn {
                this.getCollection(dataType.simpleName!!)
            }
    }

    fun withCustomCollection(
        collection: String
    )
    {
        this.collection = connection
            .useResourceWithReturn {
                this.getCollection(collection)
            }
    }

    override fun loadAllWithFilterSync(
        filter: Bson
    ): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        for (document in collection.find(filter))
        {
            entries[UUID.fromString(document.getString("_id"))!!] =
                container.serializer.deserialize(dataType, document.toJson())
        }

        return entries
    }

    override fun loadWithFilterSync(filter: Bson): D?
    {
        val document = collection.find(filter)
            .first() ?: return null

        return container.serializer.deserialize(
            dataType, document.toJson()
        )
    }

    override fun saveSync(data: D)
    {
        collection.updateOne(
            Filters.eq(
                "_id", data.identifier.toString()
            ),
            Document(
                "\$set",
                Document.parse(
                    container.serializer
                        .serialize(data)
                )
            ),
            upsetOptions
        )
    }

    override fun loadSync(identifier: UUID): D?
    {
        val document = collection.find(
            Filters.eq("_id", identifier.toString())
        ).first() ?: return null

        return container.serializer.deserialize(
            dataType, document.toJson(),
        )
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        for (document in collection.find())
        {
            entries[UUID.fromString(document.getString("_id"))!!] =
                container.serializer.deserialize(dataType, document.toJson())
        }

        return entries
    }

    override fun deleteSync(identifier: UUID)
    {
        collection.deleteOne(
            Filters.eq(
                "_id",
                identifier.toString()
            )
        )
    }
}
