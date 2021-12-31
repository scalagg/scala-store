package gg.scala.store.storage.impl

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.container.DataStoreStorableContainer
import gg.scala.store.storage.AbstractDataStoreStorageLayer
import gg.scala.store.storage.storable.AbstractStorableObject
import org.bson.Document
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass


/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class MongoDataStoreStorageLayer<D : AbstractStorableObject>(
    connection: AbstractDataStoreMongoConnection,
    private val container: DataStoreStorableContainer<D>,
    private val dataType: KClass<D>
) : AbstractDataStoreStorageLayer<AbstractDataStoreMongoConnection, D>(connection)
{
    private var collection by Delegates.notNull<MongoCollection<Document>>()
    private val upsetOptions = UpdateOptions().upsert(true)

    init
    {
        collection = connection.useResourceWithReturn {
            this.getCollection(dataType.simpleName!!)
        }
    }

    override fun saveSync(data: D)
    {
        collection.updateOne(
            Filters.eq<Any>(
                "_id", data.identifier.toString()
            ),
            Document(
                "\$set",
                Document.parse(
                    container.serializer
                        .toJson(data)
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

        return container.serializer.fromJson(
            document.toJson(), dataType.java
        )
    }

    override fun loadAllSync(): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        // TODO: 12/30/2021 possibly improve this?
        for (document in collection.find())
        {
            entries[UUID.fromString(document.getString("_id"))!!] =
                container.serializer.fromJson(document.toJson(), dataType.java)
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
