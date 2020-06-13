import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

apply(from = rootProject.file(".buildscript/configure-android-defaults.gradle"))

android {
    defaultConfig {
        applicationId = Build.applicationId + ".compose"
        multiDexEnabled = true
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
    DepsCompose.kapt.forEach { dependency ->
        kapt(dependency)
    }
    DepsCompose.testImplementation.forEach { dependency ->
        testImplementation(dependency)
    }
    DepsCompose.androidTestImplementation.forEach { dependency ->
        androidTestImplementation(dependency)
    }
}