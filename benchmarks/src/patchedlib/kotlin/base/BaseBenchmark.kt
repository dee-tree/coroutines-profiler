package base

import org.openjdk.jmh.annotations.Param
import kotlinx.coroutines.debug.DebugProbes


abstract class BaseBenchmark : Base() {

    @Param("NO_PROBES", "DEFAULT", "CREATION_ST", "SANITIZE_ST", "LAZY_CREATION_ST", "C_S", "C_L", "S_L", "C_S_L")
    override lateinit var mode: Modes

    override fun setup() {
        super.setup()

        if (mode.installedProbes) {
            DebugProbes.install()
        }

        DebugProbes.enableCreationStackTraces = mode.creationStackTrace
        DebugProbes.sanitizeStackTraces = mode.sanitizeStackTraces
        DebugProbes.lazyCreationStackTraces = mode.lazyCreationStackTraces
    }

    override fun tearDown() {
        super.tearDown()

        if (mode.installedProbes) {
            DebugProbes.uninstall()
        }
    }
}