buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(Deps.plugin_gradle)
        classpath(Deps.plugin_kotlin)
        classpath(Deps.plugin_navigation)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}
