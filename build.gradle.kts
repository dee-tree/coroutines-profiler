import java.util.*

group = "kotlinx.coroutines.profiler"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        flatDir {
            dirs(File(rootDir, "libs").absolutePath)
        }
        mavenCentral()
    }
}

val props: Properties by extra {
    Properties().apply {
        load(
            File(rootDir, "settings.properties").inputStream()
        )
    }
}

val COROUTINES_DEBUG_AGENT_PATH: String by extra {
    props["COROUTINES_DEBUG_AGENT_PATH"].toString()
}