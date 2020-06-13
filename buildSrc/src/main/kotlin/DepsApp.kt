object DepsApp {
    val implementation = arrayOf(
        Deps.preference,
        Deps.constraintLayout,
        Deps.recyclerview,
        Deps.koinAndroidXScope,
        Deps.koinAndroidXViewModel,
        Deps.work,
        Deps.room
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}