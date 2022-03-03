package gg.scala.store.serializer

import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/3/2022
 */
interface DataStoreSerializer
{
    fun serialize(`object`: Any): String

    fun <T : Any> deserialize(
        `class`: KClass<T>, input: String
    ): T
}
