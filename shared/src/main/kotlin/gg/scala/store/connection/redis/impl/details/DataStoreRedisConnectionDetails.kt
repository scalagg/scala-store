package gg.scala.store.connection.redis.impl.details

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class DataStoreRedisConnectionDetails(
    val hostname: String, val port: Int,
    val password: String? = null
)
