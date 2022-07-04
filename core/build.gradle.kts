plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

val serializationVersion: String by rootProject.extra
val patchedCoroutinesVersion: String by rootProject.extra

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${patchedCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${patchedCoroutinesVersion}")

    api(project(":core:data"))

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