plugins {
    kotlin("jvm")
    application
    id("me.champeau.jmh") version "0.6.6"
}

val originalLibProfile = "originallib"
val patchedLibProfile = "patchedlib"
var profile: String = if (project.hasProperty(patchedLibProfile)) {
    patchedLibProfile
} else {
    originalLibProfile
}


sourceSets.create(profile)

sourceSets["main"].java {
    srcDir("src/${profile}/kotlin")
}


println("current profile: $profile")

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

val coroutinesVersion: String by rootProject.extra
val patchedCoroutinesVersion: String by rootProject.extra


dependencies {
    jmh("commons-io:commons-io:2.11.0")
    jmh("org.openjdk.jmh:jmh-core:1.35")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.35")

    implementation("commons-io:commons-io:2.11.0")
    implementation("org.openjdk.jmh:jmh-core:1.35")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.35")


    when (profile) {
        originalLibProfile -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${coroutinesVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${coroutinesVersion}")
        }
        patchedLibProfile -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${patchedCoroutinesVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${patchedCoroutinesVersion}")
        }

        else -> {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${coroutinesVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${coroutinesVersion}")
        }
    }

}


val jarName: String
    get() = "${profile}-benchmark.jar"


tasks.named("jmhJar", type = Jar::class) {
    archiveFileName.set(jarName)
}

task("jmhRun", type = me.champeau.jmh.JMHTask::class) {
    println("jmhRun execution with profile ${profile}")
    jarArchive.set(File(File(project.buildDir.absoluteFile, "libs"), jarName))
    resultsFile.set(File(File(project.buildDir.absoluteFile, "results/jmh"), "${profile}-results.txt"))

    failOnError.set(true)

    dependsOn("jmhJar")
}