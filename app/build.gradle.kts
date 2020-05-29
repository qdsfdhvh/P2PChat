plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

android {
    compileSdkVersion(Build.compileSdk)
    buildToolsVersion(Build.buildTools)
    defaultConfig {
        applicationId = Build.applicationId
        minSdkVersion(Build.minSdk)
        targetSdkVersion(Build.targetSdk)
        versionCode = Build.versionCode
        versionName = Build.versionName
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
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

    implementation(Deps.support_multidex)
    implementation(Deps.support_coreKtx)
    implementation(Deps.support_appCompat)
    implementation(Deps.support_constraintLayout)
    implementation(Deps.support_preference)
//    implementation(Deps.support_preferencex)
    implementation(Deps.support_lifecycle)
    implementation(Deps.support_lifecycleKtx)
    implementation(Deps.support_viewModel)
    implementation(Deps.support_viewModelKtx)
    implementation(Deps.support_recyclerview)

    // Kotlin
    implementation(Deps.kotlin_stdlib)
    implementation(Deps.kotlinx_coroutines)
    implementation(Deps.kotlinx_io)

    // Koin注入
    implementation(Deps.koin_scope)
    implementation(Deps.koin_viewModel)

    // Navigation
    implementation(Deps.navigation_uiKtx)
    implementation(Deps.navigation_fragmentKtx)

    // WorkManager
    implementation(Deps.work_runtimeKtx)

    // DataBase
    implementation(Deps.room_ktx)
    kapt(Deps.room_compiler)

    // Prefs
    implementation(Deps.mmkv_runtime)

    // Log
    implementation(Deps.log_timber)

    // 图片
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.github.LuckSiege.PictureSelector:picture_library:v2.5.6")

    // 圆角图片
    implementation("de.hdodenhof:circleimageview:3.0.0")

    // io
    implementation("com.squareup.okio:okio:2.6.0")

    // 内存泄漏
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.3")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
