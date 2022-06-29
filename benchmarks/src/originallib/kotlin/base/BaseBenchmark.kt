package base

import kotlinx.coroutines.debug.DebugProbes


abstract class BaseBenchmark : Base() {

    override fun setup() {
        super.setup()

        if (mode.installedProbes) {
            DebugProbes.install()
        }

        DebugProbes.enableCreationStackTraces = mode.creationStackTrace
        DebugProbes.sanitizeStackTraces = mode.sanitizeStackTraces
    }

    override fun tearDown() {
        super.tearDown()

        if (mode.installedProbes) {
            DebugProbes.uninstall()
        }
    }
}