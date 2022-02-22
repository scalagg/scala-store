package gg.scala.store.bungee.settings

import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gg.scala.store.bungee.ScalaDataStoreBungee
import gg.scala.store.bungee.serializable.AbstractTypeSerializer
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KClass

/**
 * @author Foraged
 * @since 22/02/2022
 */
object BungeeSettingsProcessor
{
    val settings = mutableMapOf<KClass<*>, BungeeSettingsModel>()

    private val builder = GsonBuilder()
        .serializeNulls()

    var gson by Delegates.notNull<Gson>()

    fun initialLoad()
    {
        builder.setPrettyPrinting()
        builder.registerTypeAdapter(
            BungeeSettingsModel::class.java,
            AbstractTypeSerializer<BungeeSettingsModel>()
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
            ScalaDataStoreBungee.INSTANCE.directoryFile,
            "$id.json"
        ).reader()

        settings[T::class] = gson.fromJson(reader, T::class.java) as BungeeSettingsModel
    }

    fun saveSettingsToFile(
        kClass: KClass<*>, id: String
    )
    {
        val file = File(
            ScalaDataStoreBungee.INSTANCE.directoryFile,
            "$id.json"
        )

        if (!file.exists())
        {
            file.createNewFile()

            settings.putIfAbsent(
                kClass, kClass.java.newInstance() as BungeeSettingsModel
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
