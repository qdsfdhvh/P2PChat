import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(Deps.gradle)
        classpath(Deps.navigationSafeArgs)
        classpath(kotlin(module = "gradle-plugin", version = Versions.kotlin))
    }
}

plugins {
    id("com.gradleup.auto.manifest") version "1.0.2"
}

autoManifest {
    packageName.set(Build.applicationId)
    replaceDashesWithDot.set(true)
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }

    tasks.withType<KotlinCompile>() {
        kotlinOptions {

            jvmTarget = "1.8"

            // Don't panic, all this does is allow us to use the @OptIn meta-annotation.
            // to define our own experiments.
            freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}
