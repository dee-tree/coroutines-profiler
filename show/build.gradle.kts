plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    application
}

val serializationVersion = "1.3.2"
val ktorVersion = "1.6.7"
val reactVersion = "17.0.2-pre.299-kotlin-1.6.10"

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val serverMainClassName = "kotlinx.coroutines.profiler.show.MainKt"
application {

    mainClass.set(serverMainClassName)
}

kotlin {

    js("client", IR) {
        browser {
            binaries.executable()
        }
    }

    jvm("server") {
        compilations {
            val main by getting {
                tasks.named<Jar>("serverJar") {
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    manifest {
                        attributes["Main-Class"] = serverMainClassName
                    }

                    from(configurations.named("serverRuntimeClasspath").get()
                        .map { if (it.isDirectory) it else zipTree(it) })
                }

                tasks.named<Jar>("jar") {
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    manifest {
                        attributes["Main-Class"] = serverMainClassName
                    }

                    with(tasks["serverJar"] as CopySpec)
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }


        val serverMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation(project(":sampling"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0-SNAPSHOT")
            }
        }

        val clientMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-css:$reactVersion")

                implementation(npm("react", "17.0.2"))
                implementation(npm("react-dom", "17.0.2"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

                implementation(npm("d3-flame-graph", "4.1.3"))
                implementation(npm("d3", "7.4.3"))
            }
        }
    }
}

tasks.clean {
    doFirst {
        delete("out")
    }
}

tasks.named<Jar>("jar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "clientBrowserProductionWebpack"
    } else {
        "clientBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR

}

//distributions {
//    main {
//        contents {
//            from("$buildDir/libs") {
//                rename("${rootProject.name}-server", rootProject.name)
//                into("lib")
//            }
//        }
//    }
//}

tasks.named<JavaExec>("run") {
    jvmArgs(
        "-Dio.ktor.development=true"
    )

    args(
        "W:\\Kotlin\\Projects\\coroutines-profiler\\sample-app\\out\\results\\profile\\coroprof.json"
    )
    classpath(tasks.getByName<Jar>("jar"))
}