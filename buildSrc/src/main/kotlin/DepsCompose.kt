object DepsCompose {
    val implementation = arrayOf(
        Deps.work,
        Deps.uiFoundation,
        Deps.uiLayout,
        Deps.uiMaterial,
        Deps.uiTooling,
        Deps.uiLiveData
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}