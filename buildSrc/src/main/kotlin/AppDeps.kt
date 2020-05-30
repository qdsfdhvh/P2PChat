object AppDeps {

    val implementation = arrayOf(
        Deps.multidex,

        Deps.preference,
        Deps.constraintLayout,
        Deps.recyclerview,

        Deps.navigation,
        Deps.navigationUi,
        Deps.navigationFragment,

        Deps.work,
        Deps.mmkv,
        Deps.timber,
        Deps.glide,
        Deps.okio
    )

    val testImplementation = arrayOf(
        Deps.jUnit
    )

    val androidTestImplementation = arrayOf(
        Deps.androidJUnit,
        Deps.espresso
    )
}