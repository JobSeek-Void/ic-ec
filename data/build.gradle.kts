plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "team.jsv.data"
    compileSdk = Apps.compileSdk
    /*defaultConfig {
        buildConfigFiled("String", "API_URL", "서버 통신 URL 예정")
    }*/
}

dependencies {
    implementation(project(Modules.Domain))

    kapt(Dependencies.Hilt.Kapt)
    implementation(Dependencies.Hilt.Android)

    Dependencies.Essential.forEach(::implementation)
    Dependencies.Network.forEach(::implementation)
    Dependencies.Test.forEach(::testImplementation)


}