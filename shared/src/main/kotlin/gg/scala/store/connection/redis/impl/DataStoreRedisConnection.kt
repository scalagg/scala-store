package gg.scala.store.connection.redis.impl

import gg.scala.aware.AwareBuilder
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 4/14/2022
 */
class DataStoreRedisConnection : AbstractDataStoreRedisConnection()
{
    override fun createNewConnection() =
        AwareBuilder.of<AwareMessage>("")
            .codec(AwareMessageCodec)
            .logger(
                Logger.getLogger("DataStoreRedisConnection")
            )
            .build()
}
