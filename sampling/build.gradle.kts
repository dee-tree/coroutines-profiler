import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

repositories {
    flatDir {
        dirs(File(rootDir, "libs").absolutePath)
    }

    mavenCentral()
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0-SNAPSHOT")

    // cli arguments parser
    implementation("com.xenomachina:kotlin-argparser:2.0.7")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.clean {
    doFirst {
        delete("out")
    }
}

tasks.create<Jar>("fatJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Premain-Class"] = "kotlinx.coroutines.profiler.sampling.agent.Agent"
        attributes["Can-Redefine-Classes"] = "true"
        attributes["Can-Retransform-Classes"] = "true"
    }

    destinationDirectory.set(File(projectDir, "out/artifacts/profiler"))

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)

    archiveFileName.set("${project.name}.jar")
}
