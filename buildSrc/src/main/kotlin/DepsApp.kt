object DepsApp {
    val implementation = arrayOf(
        Deps.multidex,
        Deps.preference,
        Deps.constraintLayout,
        Deps.recyclerview,
        Deps.work,
        Deps.kotlinxIo,
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