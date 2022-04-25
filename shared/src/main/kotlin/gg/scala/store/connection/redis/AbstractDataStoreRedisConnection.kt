package gg.scala.store.connection.redis

import gg.scala.aware.Aware
import gg.scala.aware.message.AwareMessage
import gg.scala.store.connection.AbstractDataStoreConnection
import io.lettuce.core.api.StatefulRedisConnection
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreRedisConnection : AbstractDataStoreConnection<Aware<AwareMessage>, StatefulRedisConnection<String, String>>()
{
    private var handle by Delegates.notNull<Aware<AwareMessage>>()
    lateinit var connection: StatefulRedisConnection<String, String>

    private fun getAppliedResource(): StatefulRedisConnection<String, String>
    {
        return try
        {
            connection
        } catch (exception: Exception)
        {
            val connection = createNewConnection()
            setConnection(connection)

            this.connection = connection
                .internal().connect()

            this.connection
        }
    }

    override fun getConnection() = handle

    override fun useResource(lambda: StatefulRedisConnection<String, String>.() -> Unit)
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
        lambda: StatefulRedisConnection<String, String>.() -> T
    ): T?
    {
        return try
        {
            val applied = getAppliedResource()
            lambda.invoke(applied)
        } catch (exception: Exception)
        {
            LOGGER.info(exception.stackTraceToString())
            null
        }
    }

    override fun setConnection(connection: Aware<AwareMessage>)
    {
        handle = connection
    }

    override fun close()
    {
        handle.shutdown()
    }
}
