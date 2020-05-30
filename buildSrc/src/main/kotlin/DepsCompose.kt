object DepsCompose {
    val implementation = arrayOf(
        Deps.work,
        Deps.uiFoundation,
        Deps.uiLayout,
        Deps.uiMaterial,
        Deps.uiTooling
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}