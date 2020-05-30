import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        applicationId = Build.applicationId + ".compose"
        minSdkVersion(Build.minSdk)
    }
}

apply(from = rootProject.file(".buildscript/configure-compose.gradle"))
tasks.withType<KotlinCompile> {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Deps.appCompat)

    implementation(Deps.uiFoundation)
    implementation(Deps.uiLayout)
    implementation(Deps.uiMaterial)
    implementation(Deps.uiTooling)

    implementation("com.github.mvarnagiris:compose-glide-image:0.3.6")
}