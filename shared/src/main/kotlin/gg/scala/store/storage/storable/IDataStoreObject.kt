package gg.scala.store.storage.storable

import java.util.*

/**
 * Represents a generic object storable
 * in any storage layer type.
 *
 * [IDataStoreObject] instances are
 * identified through its [identifier].
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
interface IDataStoreObject
{
    val identifier: UUID
}
