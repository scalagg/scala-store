package gg.scala.store.connection.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import gg.scala.store.connection.AbstractDataStoreConnection
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
        } catch (exception: Exception)
        {
            LOGGER.info(exception.stackTraceToString())
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

    override fun setConnection(connection: MongoClient)
    {
        handle = connection
    }

    override fun close()
    {
        handle.close()
    }
}
