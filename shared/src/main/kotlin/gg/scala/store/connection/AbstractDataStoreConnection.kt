package gg.scala.store.connection

import java.io.Closeable
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreConnection<C, R> : Closeable
{
    companion object
    {
        @JvmStatic
        val LOGGER: Logger = Logger.getLogger(
            AbstractDataStoreConnection::class.simpleName
        )
    }

    abstract fun useResource(lambda: R.() -> Unit)
    abstract fun <T> useResourceWithReturn(lambda: R.() -> T): T?

    abstract fun getConnection(): C
    abstract fun setConnection(connection: C)

    abstract fun createNewConnection(): C
}
