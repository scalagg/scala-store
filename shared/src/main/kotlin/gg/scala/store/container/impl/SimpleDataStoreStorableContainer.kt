package gg.scala.store.container.impl

import gg.scala.store.container.AbstractDataStoreStorableContainer
import gg.scala.store.storage.storable.AbstractStorableObject
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 12/30/2021
 */
class SimpleDataStoreStorableContainer<D : AbstractStorableObject>(
    private val dataType: KClass<D>
) : AbstractDataStoreStorableContainer<D>()
{
    override fun getDataType() = dataType
}
