import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
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

    from(configurations.compileClasspath.get().map { if (it.isDirectory)  it else zipTree(it) })
//    from(sourceSets["main"].output.classesDirs, sourceSets["main"].compileClasspath)
    with(tasks.jar.get() as CopySpec)

    archiveFileName.set("${project.name}.jar")
}

tasks["fatJar"].dependsOn("build")