import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

apply(from = rootProject.file(".buildscript/configure-android-defaults.gradle"))

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

android {
    defaultConfig {
        applicationId = Build.applicationId
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            res.setSrcDirs(listOf(
                "src/main/res-selector",
                "src/main/res"
            ))
        }
    }
    resourcePrefix("wechat_") // 仅因个人需要
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":core:data"))
    implementation(project(":core:feature"))
    AppDeps.implementation.forEach { dependency ->
        implementation(dependency)
    }
    AppDeps.kapt.forEach { dependency ->
        kapt(dependency)
    }
    AppDeps.testImplementation.forEach { dependency ->
        testImplementation(dependency)
    }
    AppDeps.androidTestImplementation.forEach { dependency ->
        androidTestImplementation(dependency)
    }

    // 图片选择
    implementation("com.github.LuckSiege.PictureSelector:picture_library:v2.5.6")

    // 圆角图片
    implementation("de.hdodenhof:circleimageview:3.0.0")

    // 内存泄漏
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.3")
}
