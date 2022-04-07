
plugins {
//    kotlin("js") version "1.6.10"
    kotlin("jvm") version "1.6.10"
    application
    kotlin("plugin.serialization") version "1.6.10"
}

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    flatDir {
        dirs(File(rootDir, "libs").absolutePath)
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":sampling"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.3.2")
    implementation("com.jakewharton.picnic:picnic:0.6.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.0-SNAPSHOT")

}

val mainClassQualifiedName = "kotlinx.coroutines.profiler.visual.MainKt"


//application {
//    mainClass.set(mainClassQualifiedName)
//}

//tasks.create<JavaExec>("runWithLastDump") {
//    args(
//        "W:\\Kotlin\\Projects\\coroutines-profiler\\sample-app\\out\\results\\profile\\profiling_results.json"
//    )
//
//    println("Srcss: ${sourceSets.names}")
//
//    classpath(sourceSets["main"].runtimeClasspath)
//    mainClass.set(mainClassQualifiedName)
//}
//
//tasks.create("runAllCycle") {
//        dependsOn(":sample-app:runWithProfiler")
//        dependsOn(":visualization:runWithLastDump")
//}