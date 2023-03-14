import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "team.jsv.data"
    compileSdk = Apps.compileSdk

    defaultConfig {
        buildConfigField("String", "API_URL",
            gradleLocalProperties(rootDir).getProperty("server.key"))
    }
}

dependencies {
    implementation(project(Modules.Domain))
    implementation(project(Modules.UtilKotlin))

    kapt(Dependencies.Hilt.Kapt)
    implementation(Dependencies.Hilt.Android)

    Dependencies.Essential.forEach(::implementation)
    Dependencies.Network.forEach(::implementation)
    Dependencies.Test.forEach(::testImplementation)
}