import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.6.7"


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-SNAPSHOT")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")


    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    testImplementation(kotlin("test"))
}

val COROUTINES_DEBUG_AGENT_PATH: String by rootProject.extra
//val props = Properties()
//file("settings.properties").inputStream().let { props.load(it) }

tasks.test {
    useJUnitPlatform()

    jvmArgs(
        "-javaagent:$COROUTINES_DEBUG_AGENT_PATH",
//        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
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
//        "-javaagent:${props["COROUTINES_DEBUG_AGENT_PATH"]}",
        "-javaagent:$COROUTINES_DEBUG_AGENT_PATH}",
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