plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Build.compileSdk)
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:feature"))
    implementation(project(":core:resource"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}