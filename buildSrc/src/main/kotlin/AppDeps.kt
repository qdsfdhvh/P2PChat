object AppDeps {

    val implementation = arrayOf(
        Deps.multidex,
        Deps.appCompat,
        Deps.preference,
        Deps.constraintLayout,
        Deps.recyclerview,

        Deps.androidLifecycle,
        Deps.androidLifecycleKtx,
        Deps.androidViewModel,
        Deps.androidViewModelKtx,

        Deps.kotlin,
        Deps.kotlinExtensions,
        Deps.kotlinxCoroutines,
        Deps.kotlinxIo,

        Deps.koinAndroidXScope,
        Deps.koinAndroidXViewModel,

        Deps.room,

        Deps.navigation,
        Deps.navigationUi,
        Deps.navigationFragment,

        Deps.work,
        Deps.mmkv,
        Deps.timber,
        Deps.glide,
        Deps.okio
    )

    val kapt = arrayOf(
        Deps.roomCompiler
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}