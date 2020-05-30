plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
    DepsCoreData.api.forEach { dependency ->
        api(dependency)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}