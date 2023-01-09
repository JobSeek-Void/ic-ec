plugins {
    id("com.android.application") version Versions.Essential.Gradle apply false
    id("com.android.library") version Versions.Essential.Gradle apply false
    id("org.jetbrains.kotlin.android") version Versions.Essential.Kotlin apply false
    id("org.jetbrains.kotlin.jvm") version Versions.Essential.Kotlin apply false
    id("org.jetbrains.kotlin.kapt") version Versions.Essential.Kotlin apply false
    id("com.google.dagger.hilt.android") version Versions.Jetpack.Hilt apply false
}