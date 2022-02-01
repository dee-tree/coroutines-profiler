import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("kotlinx.coroutines.profiler.app.MainKt")
}

val coroutinesCoreAgentPath = "C:\\Users\\Dmitriy\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlinx\\kotlinx-coroutines-core-jvm\\1.6.0\\f3b8fd26c2e76d2f18cbc36aacb6e349fcb9fd5f\\kotlinx-coroutines-core-jvm-1.6.0.jar"
val coroutinesDebugAgentPath = "C:\\Users\\Dmitriy\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlinx\\kotlinx-coroutines-debug\\1.6.0\\aff74c5052196341f29080c718bff2375bdf5669\\kotlinx-coroutines-debug-1.6.0.jar"

tasks.create<JavaExec>("runWithProfiler") {
    classpath(sourceSets["main"].runtimeClasspath)
    mainClass.set("kotlinx.coroutines.profiler.app.MainKt")

    jvmArgs(
        "-javaagent:$coroutinesCoreAgentPath",
        "-javaagent:$coroutinesDebugAgentPath",
        "-javaagent:${rootProject.childProjects["sampling"]!!.projectDir}\\out\\artifacts\\profiler\\sampling.jar"
    )
}
