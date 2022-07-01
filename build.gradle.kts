plugins {
    kotlin("jvm") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

val serializationVersion: String by extra {
    "1.3.2"
}

val coroutinesVersion: String by extra {
    "1.6.0"
}

val patchedCoroutinesVersion: String by extra {
    "1.6.0-SNAPSHOT"
}