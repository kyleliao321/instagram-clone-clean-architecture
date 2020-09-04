# Instagram Clone - Clean Architecture

Instagram-Clone-Clean-Architecture is my portfolio project that implemented with clean architecture 
approach. The projects that I referenced while developing can be see in Reference section.

The objective of this project is to learn how to handle large project when developing Android
application. Particularly with clean-architecture approach, making it easy to expand, build and
test.

While it takes longer to setup a project, after setting-up, the workflow is less-error-prone. Making the application much reliable and confident to develop.

## Installation

### Android Studio
- File -> New -> Project from Version Control
- Pass "https://github.com/kyleliao321/instagram-clone-clean-architecture.git" into URL block.

### Git Bash Terminal
```bash
git clone https://github.com/kyleliao321/instagram-clone-clean-architecture.git
```

### Usage
> Currently, there's no actual server for the application. It use MockRemoteDataSource internally to mock the behavior of getting data from remote. 

To mock how likely the network connection will fail when using application, go to `app/src/main/java/com/example/instagram_clone_clean_architecture/app/data/data_source` and open `MockRemoteDataSource`. Modify the value of `networkFailProbability`. By default, it's 20% chance to fail.

## Demo

- Login process

![login GIF](https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/login-flow.gif)

- Remember login process

![remember login GIF](https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/remember-login-flow.gif)

- Search user and follow/unfollow process

![search GIF](https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/search-user-follow-flow.gif)

- Check post and like process

![check post GID](https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/check-post-and-like-flow.gif)

- Add post process

![add post GIF](https://raw.githubusercontent.com/kyleliao321/instagram-clone-clean-architecture/master/assets/add-post-flow.gif)

## What I learned

> While it is not the first android project I developed, I still learn a lot and strengthen some of the best-practice concept with it.

### Clean Architecture
- Modularization with Features using [dynamic feature](https://developer.android.com/guide/app-bundle/dynamic-delivery).
- Presentation/Domain/Data dependency rule.
- Setup Base Library Module to organize common utility class/function.
- Separation of Concern with [Model-View-ViewModel](https://developer.android.com/jetpack/guide).
- [Repository Pattern](https://developer.android.com/jetpack/guide) with Local/Cache/Remote data source.
- Error Handling, especially for network fail.
- Dependency Injection with [Kodein-DI](https://github.com/Kodein-Framework/Kodein-DI).

### Build System
- Using [Gradle-Kotlin-DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).
- Using buildSrc to organize library/plugin dependency and android configuration.

### Unit Test
- Using [Mockk](https://mockk.io/). (kotlin-version of [Mockito](https://site.mockito.org/))
- Utilize class/function's testability with dependency injection.
- Test with [Kotlin-Coroutine](https://kotlinlang.org/docs/reference/coroutines-overview.html).

### Android Features
- Using [Shared Preference](https://developer.android.com/reference/android/content/SharedPreferences) to store key-value data.
- [Take photo](https://developer.android.com/training/camera/photobasics) from camera/gallery.
- Load image file with [ImageDecoder](https://developer.android.com/reference/android/graphics/ImageDecoder).
- [Bitmap](https://developer.android.com/reference/android/graphics/Bitmap) data processing.
- Styling with [theme/style](https://developer.android.com/guide/topics/ui/look-and-feel/themes). 
- [Material-Design-Components](https://material.io/components).
- [Data-Binding](https://developer.android.com/topic/libraries/data-binding).

### Kotlin
- Inline function with [reified](https://kotlinlang.org/docs/reference/inline-functions.html) type parameter.
- [Coroutine context/dispatcher](https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html).
- [Extension function](https://kotlinlang.org/docs/reference/extensions.html).

## Issues
> Reference projects/articles provides solid foundation for me to build this project, but there are some outdated dependency issues or special scenario that required other approaches. Here are some of the issues that I think is worth mentioning :

### 1. Gradle-6.0/buildSrc compatiblity
**Since Gradle6.0, [buildSrc is no longer visible from gradle's setting file anymore](https://docs.gradle.org/current/userguide/upgrading_version_5.html#classes_from_buildsrc_are_no_longer_visible_to_settings_scripts):**


As a result, developers have to make sure plugin-version and modules are synchronized between gradle setting and buildSrc. [(More on the this issue)](https://github.com/gradle/gradle/issues/11090)


### 2. Navigation between dynamic features

**When navigating between features, [safe-args](https://developer.android.com/guide/navigation/navigation-pass-data) navigation is not possible:**

One approach mention in [this article](https://medium.com/google-developer-experts/using-navigation-architecture-component-in-a-large-banking-app-ac84936a42c2) is to use commom-android module to store desination ids, but there are some drawbacks (See more on the article). My approach is to let navigation's action have the id that is the same as its destination. This way, I can use the following code to capture the exception and re-navigate in traditional-way:
```kotlin
try {
  navController.navigate(navDir)
} catch (e: Exception) {
  if (e is java.lang.IllegalArgumentException {
      navController.navigate(navDir.actionId, navDir.arguments)
  } else {
      throw e
  }
}
```

### 3. Activity/Application being referenced

**Potential memory leak when reference Activity/Application:**

In some case, the class have to use activity/application context. For example, LocalDataSource have to get SharedPreference from application context. And this lead to other class has reference on Activity/Application, a classic-way to trigger memory leak. 

To prevent from memory leak, I use [WeakReference](https://developer.android.com/reference/java/lang/ref/WeakReference) to get Activity/Application context. So when the Activity/Application is destroyed, GC can reclaim the memory.


## TODO
- Replace MockRemoteDataSource with actual API that get/upload data to/from server.
- Detect memory leak.
- Encrypt user data in SharedPreference.

## Reference
- [Android-Showcase](https://github.com/igorwojda/android-showcase) by Igor Wojda
- [Android-CleanArchitecture-Kotlin](https://github.com/android10/Android-CleanArchitecture-Kotlin) by Fernando Cejas