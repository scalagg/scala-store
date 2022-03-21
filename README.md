# Store
Extensive storage-provider wrapper including native support for lettuce and [mongodb-java-driver](https://github.com/mongodb/mongo-java-driver) written in Kotlin.
 - Since this project was originally meant to be a closed-source project using Scala's propriatary code, I didn't put usage into consideration, and it's pretty hard to work with if you don't know how to the project is structured.
   * [rawr](https://github.com/devrawr) has created a project called [honey](https://github.com/devrawr/honey), that has implementations for more storage providers and is easier to use.
   * Store is currently open-source so [GrowlyX](https://github.com/growlyx) is able to link it to his portfolio.
 - Store has not been tested in a production environment *yet*. This read-me will be updated once it has been.
   * As of now, there aren't any known issues (both performance and quality) with the project.
 - Store has only been tested in [Kotlin](https://kotlinlang.org) projects, and uses kotlin-exclusive features, such as inline functions.

## Structure
The parent project contains three modules, and uses gradle as its build management tool:
- shared
- spigot
- velocity

The shared platform contains most of the connection, controller, and platform code, while spigot & velocity contain implementations for configs and debugging tools.

Store contains `DataStoreObjectController`s, which contain arbritary methods to perform tasks within context of a specified storage provider.

Store uses Google's [Gson](https://github.com/google/gson) for serialization by default.
- There is currently no way of using other serialization providers.
- You may supply your own Gson instance by passing through the instance while creating a new controller, or using `DataStoreObjectController#provideCustomSerializer(Gson)`.

Objects which are serialized/deserialized through Store must implement `IDataStoreObject`, which contains a overridable identififer field.
- This isn't the best solution, but I personally prefer it over [rawr](https://github.com/devrawr)'s solution.
- The identifier field must be a non-null UUID.

To create a new `DataStoreObjectController`, you must access the DataStoreObjectControllerCache, which will store each controller for you.

```kt
DataStoreObjectController.create<Object>()
```

You an access the stored instance through the following methods.
```kt
DataStoreObjectController.find<Object>() // can be null
DataStoreObjectController.findNotNull<Object>() // non null
```

Controllers have several methods to access, mutate, and delete data from any storage provider:
```kt
val controller = DataStoreObjectController.findNotNull<Object>()

// basic save, remove, load
controller.save(data, DataStoreStorageType.REDIS)
controller.load(data, DataStoreStorageType.REDIS)
controller.loadAll(data, DataStoreStorageType.REDIS)
controller.delete(data, DataStoreStorageType.REDIS)

// local caching
controller.remove(id) // removes from the local cache
controller.loadAndCache(id, { Object() /* if absent */ }, DataStoreStorageType.REDIS)

// accessing internal layer
controller.useLayer<RedisDataStoreStorageLayer<Object>> { /* your code */ }
```

## Authors
- [GrowlyX](https://github.com/growlyx)
- [TehNeon](https://github.com/tehneon) (original idea from his closed-source project: Xenon DataStore)
