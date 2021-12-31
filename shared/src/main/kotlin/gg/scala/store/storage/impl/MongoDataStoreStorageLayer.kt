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
import java.util.concurrent.CompletableFuture
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

    fun loadAllWithFilter(
        filter: Bson
    ): CompletableFuture<Map<UUID, D>>
    {
        return CompletableFuture.supplyAsync {
            loadAllWithFilterSync(filter)
        }
    }

    fun loadAllWithFilterSync(
        filter: Bson
    ): Map<UUID, D>
    {
        val entries = mutableMapOf<UUID, D>()

        // TODO: 12/30/2021 possibly improve this?
        for (document in collection.find(filter))
        {
            entries[UUID.fromString(document.getString("_id"))!!] =
                container.serializer.fromJson(document.toJson(), dataType.java)
        }

        return entries
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
