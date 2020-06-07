plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        minSdkVersion(9)
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:feature"))
    implementation(Deps.kotlinxIo)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}