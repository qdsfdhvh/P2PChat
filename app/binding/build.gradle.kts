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
    buildFeatures {
        viewBinding = true
    }
    resourcePrefix("wechat_") // 仅因个人需要
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    DepsApp.implementation.forEach { dependency ->
        implementation(dependency)
    }
    DepsApp.testImplementation.forEach { dependency ->
        testImplementation(dependency)
    }
    DepsApp.androidTestImplementation.forEach { dependency ->
        androidTestImplementation(dependency)
    }
    kapt(Deps.roomCompiler)

    // 圆角图片
    implementation("de.hdodenhof:circleimageview:3.0.0")

    // 内存泄漏
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.3")

    implementation(project(":libs:p2p"))
    implementation(project(":libs:picture-selector"))
}
