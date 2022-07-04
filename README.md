# Coroutines profiler

### kotlinx.coroutines profiler is a sampling profiler which captures all living coroutines, their states, suspension points, threads.

---

### Modules

* `core` module contains profiler, its runner (*java agent*) and dumps to file writer.
* `show` module displays profiler dumps via webpage.

### Needed dependencies

Profiler depends on modified
`kotlinx.coroutines.core` and `kotlinx.coroutines.debug` libs.
[Their fork](https://github.com/dee-tree/kotlinx.coroutines).

To achieve patched version of coroutines packages jars:

1. go to `profiler` branch: `git branch profiler`
2. build and push `core` module to *mavenLocal* repo: <br />`gradle :kotlinx-coroutines-core:publishToMavenLocal`
3. build and push `debug` module to *mavenLocal* repo: <br />`gradle :kotlinx-coroutines-debug:publishToMavenLocal`

### How to run?

1. Build `core` module: `gradle :core:fatJar`
2. Attach profiler's *JAR* (at *core/out/artifacts/profiler/profiler.jar*) to your application as agent via jvm
   args: `-javaagent:PATH_TO_PROFILER_JAR=PROFILER_ARGS`

Or you can run profiler on `sample-app` via `gradle :sample-app:runWithProfiler`

### Profiler CLI arguments

* Directory to output file: `-o OUTPUT_DIR`. Profiling results will be written at `OUTPUT_DIR/coroprof.json`. Also, dump
  probes and coroutines structure will be written at the same directory: `OUTPUT_DIR/coroprof_probes.json`
  and `OUTPUT_DIR/coroprof_struct.json`
* Dumps interval in milliseconds: `-i INT_VALUE`
* Collect internal statistics of profiling: `-s`