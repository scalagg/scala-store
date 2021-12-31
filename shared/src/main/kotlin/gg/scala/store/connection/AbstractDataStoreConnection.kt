package gg.scala.store.connection

import com.sun.istack.internal.logging.Logger
import java.io.Closeable

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
            AbstractDataStoreConnection::class.java
        )
    }

    abstract fun useResource(lambda: R.() -> Unit)
    abstract fun <T> useResourceWithReturn(lambda: R.() -> T): T?

    abstract fun getConnection(): C
    abstract fun setConnection(connection: C)

    abstract fun createNewConnection(): C
}
