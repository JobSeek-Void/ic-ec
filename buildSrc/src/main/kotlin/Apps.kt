import org.gradle.api.JavaVersion

object Apps {
    // android 블럭
    const val compileSdk = 33

    // defaultConfig 블럭
    const val minSdk = 23
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "1.0"

    // compileOptions 블럭
    val sourceCompatibility = JavaVersion.VERSION_11
    val targetCompatibility = JavaVersion.VERSION_11

    // kotlinOptions 블럭
    const val jvmTarget = "1.8"
}