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

    const val uiFoundation = "androidx.ui:ui-foundation:${Versions.compose}"
    const val uiLayout     = "androidx.ui:ui-layout:${Versions.compose}"
    const val uiMaterial   = "androidx.ui:ui-material:${Versions.compose}"
    const val uiTooling    = "androidx.ui:ui-tooling:${Versions.compose}"
    const val uiLiveData   = "androidx.ui:ui-livedata:${Versions.compose}"

    const val work = "androidx.work:work-runtime-ktx:${Versions.work}"
    const val mmkv = "com.tencent:mmkv-static:${Versions.mmkv}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val okio = "com.squareup.okio:okio:${Versions.okio}"
}