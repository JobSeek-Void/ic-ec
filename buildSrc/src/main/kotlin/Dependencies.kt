object Dependencies {

    object Hilt {
        const val Kapt = "com.google.dagger:hilt-android-compiler:${Versions.Jetpack.Hilt}"
        const val Android = "com.google.dagger:hilt-android:${Versions.Jetpack.Hilt}"
        const val Core = "com.google.dagger:hilt-core:${Versions.Jetpack.Hilt}"
    }

    object Glide {
        const val Annotation = "com.github.bumptech.glide:compiler:${Versions.Glide.Glide}"
    }

    val Essential = listOf(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Essential.Coroutine}"
    )

    val Network = listOf(
        "com.squareup.retrofit2:retrofit:${Versions.Network.Retrofit}",
        "com.squareup.retrofit2:converter-gson:${Versions.Network.Retrofit}",
        "com.squareup.okhttp3:okhttp:${Versions.Network.OkHttp}",
        "com.squareup.okhttp3:logging-interceptor:${Versions.Network.OkHttp}",
    )

    val Test = listOf(
        "junit:junit:${Versions.Test.JUnit}",
        "io.mockk:mockk:${Versions.Test.MockK}",
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Test.Coroutine}",
    )

    val Ktx = listOf(
        "androidx.core:core-ktx:${Versions.Ktx.Core}",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Ktx.Lifecycle}",
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.Ktx.Lifecycle}",
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Ktx.Lifecycle}",
    )

    val CameraX = listOf(
        "androidx.camera:camera-core:${Versions.Jetpack.CameraX}",
        "androidx.camera:camera-view:${Versions.Jetpack.CameraX}",
        "androidx.camera:camera-camera2:${Versions.Jetpack.CameraX}",
        "androidx.camera:camera-lifecycle:${Versions.Jetpack.CameraX}",
    )

    val UI = listOf(
        "androidx.appcompat:appcompat:${Versions.UI.Appcompat}",
        "androidx.constraintlayout:constraintlayout:${Versions.UI.ConstraintLayout}",
        "androidx.navigation:navigation-fragment-ktx:${Versions.Jetpack.Navigation}",
        "androidx.navigation:navigation-ui-ktx:${Versions.Jetpack.Navigation}",
        "com.github.bumptech.glide:glide:${Versions.Glide.Glide}",
        "com.google.android.material:material:${Versions.UI.Material}",
        "com.github.chrisbanes:PhotoView:${Versions.UI.PhotoView}",
    )

}