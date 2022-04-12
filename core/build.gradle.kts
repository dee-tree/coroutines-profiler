plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0-SNAPSHOT")

    implementation(project(":core:data"))

    // cli arguments parser
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
}

tasks.register<Jar>("fatJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Premain-Class"] = "kotlinx.coroutines.profiler.core.agent.Agent"
        attributes["Can-Redefine-Classes"] = "true"
        attributes["Can-Retransform-Classes"] = "true"
    }

    destinationDirectory.set(File(projectDir, "out/artifacts/profiler"))

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)

    archiveFileName.set("profiler.jar")
}

tasks.clean {
    doFirst {
        delete("out")
    }
}