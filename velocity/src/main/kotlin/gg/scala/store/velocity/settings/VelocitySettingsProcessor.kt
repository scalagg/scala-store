package gg.scala.store.velocity.settings

import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gg.scala.store.velocity.ScalaDataStoreVelocity
import gg.scala.store.velocity.serializable.AbstractTypeSerializer
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/15/2021
 */
object VelocitySettingsProcessor
{
    val settings = mutableMapOf<KClass<*>, VelocitySettingsModel>()

    private val builder = GsonBuilder()
        .serializeNulls()

    var gson by Delegates.notNull<Gson>()

    fun initialLoad()
    {
        builder.setPrettyPrinting()
        builder.registerTypeAdapter(
            VelocitySettingsModel::class.java,
            AbstractTypeSerializer<VelocitySettingsModel>()
        )

        gson = builder.create()
    }

    inline fun <reified T> locate(): T?
    {
        return settings[T::class] as T
    }

    inline fun <reified T> loadSettings(id: String)
    {
        saveSettingsToFile(T::class, id)

        val reader = File(
            ScalaDataStoreVelocity.INSTANCE.directoryFile,
            "$id.json"
        ).reader()

        settings[T::class] = gson.fromJson(reader, T::class.java) as VelocitySettingsModel
    }

    fun saveSettingsToFile(
        kClass: KClass<*>, id: String
    )
    {
        val file = File(
            ScalaDataStoreVelocity.INSTANCE.directoryFile,
            "$id.json"
        )

        if (!file.exists())
        {
            file.createNewFile()

            settings.putIfAbsent(
                kClass, kClass.java.newInstance() as VelocitySettingsModel
            )
        }

        if (settings[kClass] != null)
        {
            Files.write(
                gson.toJson(
                    settings[kClass], kClass.java
                ),
                file, Charsets.UTF_8
            )
        }
    }
}
