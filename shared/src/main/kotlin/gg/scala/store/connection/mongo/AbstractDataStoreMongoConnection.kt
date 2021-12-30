package gg.scala.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import gg.scala.store.connection.AbstractDataStoreConnection
import redis.clients.jedis.JedisPool
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreMongoConnection : AbstractDataStoreConnection<MongoClient, MongoDatabase>()
{
    internal var handle by Delegates.notNull<MongoClient>()


}
