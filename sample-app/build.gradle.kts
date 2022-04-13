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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-SNAPSHOT")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()

    jvmArgs(
        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
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

val props = Properties()
file("settings.properties").inputStream().let { props.load(it) }


tasks.create<JavaExec>("runWithProfiler") {
    dependsOn(":core:fatJar")
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set(mainClassQualifiedName)

    val args = "-o \"out/results/profile\" -i 4 -s "
    val agentPath = "${rootProject.childProjects["core"]!!.projectDir}${File.separator}out${File.separator}artifacts${File.separator}profiler${File.separator}profiler.jar"

    jvmArgs(
        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
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
