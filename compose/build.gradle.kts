import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
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
    implementation("com.github.mvarnagiris:compose-glide-image:0.3.6")
    implementation("app.cash.contour:contour:0.1.7")
}