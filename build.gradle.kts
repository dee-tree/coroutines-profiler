

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        flatDir {
            dirs(File(rootDir, "libs").absolutePath)
        }
//        mavenCentral()
    }
}