package gg.scala.store.connection.mongo.impl.details

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class DataStoreMongoConnectionDetails(
    val uri: String,
    var database: String = "Scala"
)
