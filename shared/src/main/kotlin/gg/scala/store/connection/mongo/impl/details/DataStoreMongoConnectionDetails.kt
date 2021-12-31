package gg.scala.store.connection.mongo.impl.details

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class DataStoreMongoConnectionDetails
@JvmOverloads
constructor(
    val uri: String = "mongodb://127.0.0.1:27017/admin",
    var database: String = "Scala"
)
