plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "team.jsv.presentation"
    compileSdk = Apps.compileSdk

    defaultConfig {
        minSdk = Apps.minSdk
    }

    compileOptions {
        sourceCompatibility = Apps.sourceCompatibility
        targetCompatibility = Apps.targetCompatibility
    }

    kotlinOptions {
        jvmTarget = Apps.jvmTarget
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(Modules.Domain))
    implementation(project(Modules.UtilKotlin))

    kapt(Dependencies.Hilt.Kapt)
    implementation(Dependencies.Hilt.Android)

    annotationProcessor(Dependencies.Glide.Annotation)

    Dependencies.Essential.forEach(::implementation)
    Dependencies.Ktx.forEach(::implementation)
    Dependencies.CameraX.forEach(::implementation)
    Dependencies.UI.forEach(::implementation)

}