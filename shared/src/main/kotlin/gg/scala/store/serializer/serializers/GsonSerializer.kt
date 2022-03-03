package gg.scala.store.serializer.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.store.serializer.DataStoreSerializer
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/3/2022
 */
object GsonSerializer : DataStoreSerializer
{
    @JvmStatic
    private val GSON = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls().create()

    private var supplier = { GSON }

    fun provideCustomGson(
        lambda: () -> Gson
    )
    {
        supplier = lambda
    }

    override fun serialize(`object`: Any): String
    {
        return supplier.invoke()
            .toJson(`object`)
    }

    override fun <T : Any> deserialize(
        `class`: KClass<T>, input: String
    ): T
    {
        return supplier.invoke()
            .fromJson(input, `class`.java)
    }
}
