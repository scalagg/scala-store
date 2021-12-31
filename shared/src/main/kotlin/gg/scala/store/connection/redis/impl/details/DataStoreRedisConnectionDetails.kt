package gg.scala.store.connection.redis.impl.details

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
data class DataStoreRedisConnectionDetails
@JvmOverloads
constructor(
    val hostname: String = "127.0.0.1",
    val port: Int = 6379,
    val password: String? = null
)
