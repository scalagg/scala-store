package gg.scala.store

import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import kotlin.properties.Delegates

/**
 * Contains code which is not
 * specific to a platform.
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

    abstract fun debug(from: String, message: String)
}

fun String.debug(from: String)
{
    ScalaDataStoreShared.INSTANCE.debug(from, this)
}
