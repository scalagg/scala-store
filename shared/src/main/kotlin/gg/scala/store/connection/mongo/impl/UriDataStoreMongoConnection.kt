package gg.scala.store.connection.mongo.impl

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
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
        return try
        {
            getConnection()
                .getDatabase(details.database)
        } catch (ignored: Exception)
        {
            setConnection(createNewConnection())

            getConnection()
                .getDatabase(details.database)
        }
    }

    override fun getConnection(): MongoClient
    {
        TODO("Not yet implemented")
    }

    override fun createNewConnection(): MongoClient
    {
        return MongoClient(
            MongoClientURI(details.uri)
        )
    }
}
