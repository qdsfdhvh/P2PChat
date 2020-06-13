object DepsCompose {
    val implementation = arrayOf(
        Deps.work,
        Deps.uiFoundation,
        Deps.uiLayout,
        Deps.uiMaterial,
        Deps.uiTooling,
        Deps.uiLiveData,
        Deps.daggerHiltAndroid,
        Deps.hiltCommon,
        Deps.hiltViewModel
    )

    val kapt = arrayOf(
        Deps.daggerHiltCompiler,
        Deps.hiltCompiler
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}