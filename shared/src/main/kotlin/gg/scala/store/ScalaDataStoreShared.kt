package gg.scala.store

import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import kotlin.properties.Delegates

/**
 * Holds all non platform-specific
 * information.
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class ScalaDataStoreShared
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<ScalaDataStoreShared>()
    }

    abstract fun getRedisConnection(): AbstractDataStoreRedisConnection
    abstract fun getMongoConnection(): AbstractDataStoreMongoConnection
}
