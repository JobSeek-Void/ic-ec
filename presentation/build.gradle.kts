plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "team.jsv.presentation"
    compileSdk = Apps.compileSdk

    compileOptions {
        sourceCompatibility = Apps.sourceCompatibility
        targetCompatibility = Apps.targetCompatibility
    }

    kotlinOptions {
        jvmTarget = Apps.jvmTarget
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(Modules.Domain))

    kapt(Dependencies.Hilt.Kapt)
    implementation(Dependencies.Hilt.Android)

    Dependencies.Essential.forEach(::implementation)
    Dependencies.Ktx.forEach(::implementation)
    Dependencies.CameraX.forEach(::implementation)
    Dependencies.UI.forEach(::implementation)
    Dependencies.JitPack.forEach(::implementation)
    Dependencies.Glide.forEach(::implementation)
}