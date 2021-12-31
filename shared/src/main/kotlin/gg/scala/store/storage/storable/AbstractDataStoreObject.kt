package gg.scala.store.storage.storable

import java.util.*

/**
 * Represents a generic object storable
 * in any storage layer type.
 *
 * [AbstractDataStoreObject] instances are
 * identified through its [identifier].
 *
 * @author GrowlyX
 * @since 12/30/2021
 */
abstract class AbstractDataStoreObject
{
    abstract val identifier: UUID
}
