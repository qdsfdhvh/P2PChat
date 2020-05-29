object Deps {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"

    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val androidJUnit = "androidx.test.ext:junit:${Versions.androidJUnitExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"

    const val multidex = "androidx.multidex:multidex:${Versions.multidex}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val preference = "androidx.preference:preference:${Versions.preference}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"

//    const val support_preferencex = "com.takisoft.preferencex:preferencex:${Versions.preference}"
//    const val support_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
//    const val support_runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
//    const val support_pagingKtx = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
//    const val support_material = "com.google.android.material:material:${Versions.material}"

    const val androidLifecycle    = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle}"
    const val androidLifecycleKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val androidViewModel    = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle}"
    const val androidViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val kotlinExtensions  = "androidx.core:core-ktx:${Versions.kotlinExtensions}"
    const val kotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinxCoroutines}"
    const val kotlinxIo         = "org.jetbrains.kotlinx:kotlinx-io-jvm:${Versions.kotlinxIo}"

    const val koinAndroidXScope = "org.koin:koin-androidx-scope:${Versions.koin}"
    const val koinAndroidXViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    const val room = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val navigation = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"

    const val work = "androidx.work:work-runtime-ktx:${Versions.work}"
    const val mmkv = "com.tencent:mmkv-static:${Versions.mmkv}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val okio = "com.squareup.okio:okio:${Versions.okio}"
}