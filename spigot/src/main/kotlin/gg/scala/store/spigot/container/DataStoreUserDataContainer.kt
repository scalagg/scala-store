package gg.scala.store.spigot.container

import gg.scala.store.container.impl.SimpleDataStoreStorableContainer

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
object DataStoreUserDataContainer : SimpleDataStoreStorableContainer<DataStoreUserData>(DataStoreUserData::class)
