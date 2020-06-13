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
    implementation("com.github.LuckSiege.PictureSelector:picture_library:v2.5.6")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}