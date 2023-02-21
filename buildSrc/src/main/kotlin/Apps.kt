import org.gradle.api.JavaVersion

object Apps {
    // android
    const val compileSdk = 33

    // defaultConfig
    const val minSdk = 23
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "1.0"

    // compileOptions
    val sourceCompatibility = JavaVersion.VERSION_11
    val targetCompatibility = JavaVersion.VERSION_11

    // kotlinOptions
    const val jvmTarget = "1.8"
}
