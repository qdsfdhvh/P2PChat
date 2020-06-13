import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

apply(from = rootProject.file(".buildscript/configure-android-defaults.gradle"))

android {
    defaultConfig {
        applicationId = Build.applicationId + ".compose"
    }
}

apply(from = rootProject.file(".buildscript/configure-compose.gradle"))
tasks.withType<KotlinCompile> {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    DepsCompose.implementation.forEach { dependency ->
        implementation(dependency)
    }
    DepsCompose.testImplementation.forEach { dependency ->
        testImplementation(dependency)
    }
    DepsCompose.androidTestImplementation.forEach { dependency ->
        androidTestImplementation(dependency)
    }
}