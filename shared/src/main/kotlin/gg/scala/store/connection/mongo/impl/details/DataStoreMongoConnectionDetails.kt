package gg.scala.store.connection.mongo.impl.details

import com.mongodb.MongoClientURI

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class DataStoreMongoConnectionDetails(
    val uri: MongoClientURI,
    var database: String = "Scala"
)
{
    companion object
    {
        @JvmStatic
        fun of(uri: String): DataStoreMongoConnectionDetails
        {
            return DataStoreMongoConnectionDetails(
                MongoClientURI(uri)
            )
        }
    }
}
