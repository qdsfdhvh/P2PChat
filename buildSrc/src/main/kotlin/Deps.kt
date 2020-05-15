object Deps {
    const val plugin_gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val plugin_kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val plugin_navigation = "android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0"

    const val support_multidex = "androidx.multidex:multidex:${Versions.multidex}"
    const val support_coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val support_appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val support_constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val support_preference = "androidx.preference:preference:${Versions.preference}"
    const val support_preferencex = "com.takisoft.preferencex:preferencex:${Versions.preference}"
    const val support_preferencex_simplemenu = "com.takisoft.preferencex:preferencex-simplemenu:${Versions.preference}"
    const val support_lifecycle = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle}"
    const val support_lifecycleKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    const val support_viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle}"
    const val support_viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val support_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val support_runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val support_pagingKtx = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    const val support_material = "com.google.android.material:material:${Versions.material}"
    const val support_recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"

    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val kotlin_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val koin_scope = "org.koin:koin-androidx-scope:${Versions.koin}"
    const val koin_viewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
    const val koin_fragment = "org.koin:koin-androidx-fragment:${Versions.koin}"
    const val koin_ext = "org.koin:koin-androidx-ext:${Versions.koin}"

    const val mmkv_runtime = "com.tencent:mmkv-static:${Versions.mmkv}"

    const val network_retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val network_retrofitMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"

    const val moshi_adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshi_core = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshi_compiler = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    const val arouter_api = "com.alibaba:arouter-api:${Versions.arouterApi}"
    const val arouter_compiler = "com.alibaba:arouter-compiler:${Versions.arouterCompiler}"

    const val room_ktx = "androidx.room:room-ktx:${Versions.room}"
    const val room_compiler = "androidx.room:room-compiler:${Versions.room}"

    const val work_runtimeKtx = "androidx.work:work-runtime-ktx:${Versions.work}"

    const val navigation_uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigation_fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"

    const val cc_api = "com.billy.android:cc:${Versions.ccApi}"

    const val log_timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val glide_runtime = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
}