plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

val serializationVersion: String by rootProject.extra

kotlin {

    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${serializationVersion}")
            }
        }
    }
}

tasks.clean {
    doFirst {
        delete("out")
    }
}