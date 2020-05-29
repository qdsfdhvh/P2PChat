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

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }

    tasks.withType<KotlinCompile>() {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
