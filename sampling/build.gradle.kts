import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create<Jar>("fatJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Premain-Class"] = "kotlinx.coroutines.profiler.sampling.Agent"
        attributes["Can-Redefine-Classes"] = "true"
    }

    destinationDirectory.set(File(projectDir, "out/artifacts/profiler"))
    archiveVersion.set("")
    archiveBaseName.set(project.name)

    from(sourceSets["main"].output.classesDirs, sourceSets["main"].compileClasspath)
    with(tasks.jar.get() as CopySpec)
}

tasks["fatJar"].dependsOn("build")