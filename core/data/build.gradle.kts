plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
    DataDeps.api.forEach { dependency ->
        api(dependency)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}