package gg.scala.store.connection.mongo.impl

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class UriDataStoreMongoConnection(
    private val details: DataStoreMongoConnectionDetails
) : AbstractDataStoreMongoConnection()
{
    override fun getAppliedResource(): MongoDatabase
    {
        return handle.getDatabase(details.database)
    }

    override fun createNewConnection(): MongoClient
    {
        return MongoClient(details.uri)
    }
}
