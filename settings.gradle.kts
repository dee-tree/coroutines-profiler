rootProject.name = "coroutines-profiler"


include(":sample-app")
include("visualization")
include("show")
include("core")
include("core:data")
include("benchmarks")
include("benchmarks:benchmark-original-lib")
findProject(":benchmarks:benchmark-original-lib")?.name = "benchmark-original-lib"
include("benchmarks:benchmark-patched-lib")
findProject(":benchmarks:benchmark-patched-lib")?.name = "benchmark-patched-lib"
