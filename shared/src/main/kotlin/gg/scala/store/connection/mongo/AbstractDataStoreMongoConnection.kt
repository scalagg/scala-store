package gg.scala.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import gg.scala.store.connection.AbstractDataStoreConnection
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisException
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreMongoConnection : AbstractDataStoreConnection<MongoClient, MongoDatabase>()
{
    internal var handle by Delegates.notNull<MongoClient>()

    abstract fun getAppliedResource(): MongoDatabase

    override fun useResource(lambda: MongoDatabase.() -> Unit)
    {
        try
        {
            val applied = getAppliedResource()
            lambda.invoke(applied)
        } catch (exception: JedisException)
        {
            LOGGER.logSevereException(exception)
        }
    }

    override fun <T> useResourceWithReturn(
        lambda: MongoDatabase.() -> T
    ): T
    {
        return lambda.invoke(
            getAppliedResource()
        )
    }

    override fun getConnection() = handle

    override fun setConnection(connection: MongoClient)
    {
        try
        {
            close()
        } catch (exception: Exception)
        {
            LOGGER.logSevereException(exception)
        }

        handle = connection
    }

    override fun close()
    {
        handle.close()
    }
}
