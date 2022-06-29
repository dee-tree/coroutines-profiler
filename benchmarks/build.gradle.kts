plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("me.champeau.jmh") version "0.6.6"
}

val COROUTINES_DEBUG_AGENT_PATH: String by rootProject.extra

val originalLibProfile = "originallib"
val patchedLibProfile = "patchedlib"
var profile: String? = null

if (project.hasProperty(originalLibProfile)) {
    profile = originalLibProfile

} else if (project.hasProperty(patchedLibProfile)) {
    profile = patchedLibProfile
}

profile?.let {
    sourceSets["jmh"].java.srcDirs("src/${it}/kotlin")
}

configurations.asMap.also { println("configs: $it") }
sourceSets["jmh"].allSource.also { srcs -> println("sources of jmh: ${srcs.srcDirs}") }
sourceSets["jmh"].allSource.also { srcs -> println("sources of jmh: ${srcs.elements.get()}") }

println(profile?.let { "current profile: $it" } ?: "run without profile")

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"


dependencies {
    jmh("commons-io:commons-io:2.11.0")
    jmh("org.openjdk.jmh:jmh-core:1.35")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.35")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.openjdk.jmh:jmh-core:1.35")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.35")


    when (profile) {
        originalLibProfile -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0")
        }
        patchedLibProfile -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-SNAPSHOT")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0-SNAPSHOT")
        }
        else -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0")
        }
    }

}


val jarName: String
    get() = "${profile}-benchmark.jar"


tasks.named("jmhJar", type = Jar::class) {
    archiveFileName.set(jarName)
}

task("jmhRun", type = me.champeau.jmh.JMHTask::class) {
    println("profile in jmhRun: ${profile}")
    jarArchive.set(File(File(project.buildDir.absoluteFile, "libs"), jarName))
    resultsFile.set(File(File(project.buildDir.absoluteFile, "results/jmh"), "${profile}-results.txt"))

    jvmArgs.set(listOf("-javaagent:${COROUTINES_DEBUG_AGENT_PATH}"))

    failOnError.set(true)

    dependsOn("jmhJar")
}