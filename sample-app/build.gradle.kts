import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.6.7"

val coroutinesVersion: String by rootProject.extra
val patchedCoroutinesVersion: String by rootProject.extra


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core") {
        because("patched version of lib with lazy creation stack traces")
        version {
            strictly("1.6.0-SNAPSHOT")
        }
    }

    constraints {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${patchedCoroutinesVersion}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutinesVersion}") {
            because("patch influenced only -core and -debug packages")
        }
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm") {
            because("patched -core package must be SNAPSHOT version")
            version {
                strictly(patchedCoroutinesVersion)
            }
        }
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${coroutinesVersion}") {
            because("patch influenced only -core and -debug packages")
        }

    }


    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()

    jvmArgs(
        "-javaagent:${rootProject.childProjects["core"]!!.projectDir}${File.separator}out${File.separator}artifacts${File.separator}profiler${File.separator}sampling.jar",
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val mainClassQualifiedName = "kotlinx.coroutines.profiler.app.MainKt"

application {
    mainClass.set(mainClassQualifiedName)
}

tasks.create<JavaExec>("runWithProfiler") {
    dependsOn(":core:fatJar")
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set(mainClassQualifiedName)

    val args = "-o out/results/profile -i 4 -s "
    val agentPath =
        "${rootProject.childProjects["core"]!!.projectDir}${File.separator}out${File.separator}artifacts${File.separator}profiler${File.separator}profiler.jar"

    jvmArgs(
        "-javaagent:$agentPath=$args"
    )
}

tasks.clean {
    doFirst {
        delete("out")
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = mainClassQualifiedName
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks["test"].dependsOn(":core:fatJar")