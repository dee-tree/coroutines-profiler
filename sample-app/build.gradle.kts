import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()

    jvmArgs(
        "-Xms256m",
        "-Xmx1g",
        "-javaagent:${props["COROUTINES_CORE_AGENT_PATH"]}",
        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
        "-javaagent:${rootProject.childProjects["sampling"]!!.projectDir}${File.separator}out${File.separator}artifacts${File.separator}profiler${File.separator}sampling.jar",
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("kotlinx.coroutines.profiler.app.MainKt")
}

val props = Properties()
file("settings.properties").inputStream().let { props.load(it) }


tasks.create<JavaExec>("runWithProfiler") {
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set("kotlinx.coroutines.profiler.app.MainKt")

    jvmArgs(
//        "-javaagent:${props["COROUTINES_CORE_AGENT_PATH"]}",
        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
//        "-javaagent:${rootProject.childProjects["sampling"]!!.projectDir}${File.separator}out${File.separator}artifacts${File.separator}profiler${File.separator}sampling.jar"
    )
}

tasks["runWithProfiler"].dependsOn(":sampling:fatJar")
tasks["test"].dependsOn(":sampling:fatJar")
