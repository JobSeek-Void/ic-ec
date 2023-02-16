plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
}

java {
    sourceCompatibility = Apps.sourceCompatibility
    targetCompatibility = Apps.targetCompatibility
}

dependencies {
    kapt(Dependencies.Hilt.Kapt)
    implementation(Dependencies.Hilt.Core)
    Dependencies.Essential.forEach(::implementation)
    Dependencies.Test.forEach(::testImplementation)
}