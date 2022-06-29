package base

import org.openjdk.jmh.annotations.Param
import kotlinx.coroutines.debug.DebugProbes


abstract class BaseBenchmark : Base() {

    @Param("NO_PROBES", "DEFAULT", "CREATION_ST", "SANITIZE_ST", "DELAYED_CREATION_ST", "C_S", "C_D", "S_D", "C_S_D")
    override lateinit var mode: Modes

    var delayedCreationStackTraces: Boolean = false
        private set

    override fun setup() {
        super.setup()

        if (mode.installedProbes) {
            DebugProbes.install()
        }

        DebugProbes.enableCreationStackTraces = mode.creationStackTrace
        DebugProbes.sanitizeStackTraces = mode.sanitizeStackTraces
        DebugProbes.delayedCreationStackTraces = mode.delayedCreationStackTraces
    }

    override fun tearDown() {
        super.tearDown()

        if (mode.installedProbes) {
            DebugProbes.uninstall()
        }
    }
}