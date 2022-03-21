package kotlinx.coroutines.profiler.visual

import kotlinx.coroutines.profiler.sampling.ProfilingCoroutineInfo
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
fun generateHtmlContent(coroutines: List<ProfilingCoroutineInfo>, outDir: File): File {
    val flameJson = File(outDir, "coro-stacks.json")
    coroutines.toFlameJson(flameJson.outputStream())

    val htmlFile = File(outDir, "results.html")

//    htmlFile.writeText(
//    """
//        <head>
//            <link rel="stylesheet" type="text/css"
//                  href="https://cdn.jsdelivr.net/npm/d3-flame-graph@4.1.3/dist/d3-flamegraph.css"/>
//
//            <link rel="stylesheet"
//                  href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
//
//        </head>
//        <body>
//
//        <div class="container">
//            <div class="header clearfix">
//                <nav>
//                    <div class="pull-right">
//                        <form class="form-inline" id="form">
//                            <a class="btn" href="javascript: resetZoom();">Reset zoom</a>
//                            <a class="btn" href="javascript: clear();">Clear</a>
//                            <div class="form-group">
//                                <input type="text" class="form-control" id="term"/>
//                            </div>
//                            <a class="btn btn-primary" href="javascript: search();">Search</a>
//                        </form>
//                    </div>
//                </nav>
//                <h3 class="text-muted">Coroutines Profiler results</h3>
//            </div>
//        </div>
//        <div id="chart" style="display: flex; justify-content: center; margin-bottom: 5%"></div>
//
//
//        <div id="coroutineInfo">
//            <h3 id="coroutineName" style="display: flex; justify-content: center"></h3>
//            <div id="coroutineTimeLine" style="display: flex; justify-content: center"></div>
//            <div id="stacktrace" style="display: flex; justify-content: center"></div>
//        </div>
//
//        <script type="text/javascript" src="https://d3js.org/d3.v7.js"></script>
//        <script type="text/javascript"
//                src="https://cdn.jsdelivr.net/npm/d3-flame-graph@4.1.3/dist/d3-flamegraph.min.js"></script>
//        <script type="text/javascript">
//
//            const search = () => {
//                let term = document.getElementById("term").value;
//                flameGraph.search(term);
//                coroutineStackFlameGraph?.search(term);
//            };
//
//            const clear = () => {
//                document.getElementById("term").value = "";
//                flameGraph.clear();
//                coroutineStackFlameGraph.clear();
//            };
//            const resetZoom = () => {
//                coroutineStackFlameGraph = null
//                flameGraph.resetZoom();
//            };
//
//
//            function stacksFromList(list, value, eachAction) {
//                let current = eachAction({
//                    name: list[0],
//                    value: value,
//                });
//
//                let currentIdx = 1
//                while (currentIdx < list.length) {
//                    current = eachAction({
//                        name: list[currentIdx++],
//                        value: value,
//                        children: [current]
//                    })
//                }
//
//                return current
//            }
//
//            function appendStackTrace(to, what) {
//                let current = to
//                while (current.hasOwnProperty('children')) {
//                    current = current.children[0]
//                }
//                current.children = [what]
//
//                return to
//            }
//
//            function coroutineStatesColorMapper(d, originalColor) {
//                if (d.highlight) return "#6c84d0"
//
//                if (d.data.state === "CREATED")
//                    return "#FFB266"
//                if (d.data.state === "RUNNING")
//                    return "#70ff66"
//                if (d.data.state === "SUSPENDED")
//                    return "#FF6666"
//
//                return originalColor
//            }
//
//            let coroutineStackFlameGraph
//            let coroutineTimeLine
//
//            const onClick = d => {
//                let divParent = document.getElementById("stacktrace");
//                let child = divParent.lastElementChild
//                while (child) {
//                    divParent.removeChild(child)
//                    child = divParent.lastElementChild
//                }
//
//                if (d.data.name === "root")
//                    return
//
//                coroutineTimeLine = flamegraph()
//
//                fetch("${flameJson.name}")
//                    .then(response => response.json())
//                    .then(json => {
//
//                        console.log(JSON.stringify(json.filter(coro => coro.id == d.data.id")))
//                    })
//
//
//                coroutineStackFlameGraph = flamegraph()
//                    .title("Stacktrace for " + d.data.name + " at " + d.data.state)
//
//                document.getElementById("coroutineName").innerHTML = `Coroutine ${'$'}{d.data.name}`;
//
//
//                coroutineStackFlameGraph.setColorMapper(function (d, originalColor) {
//                    if (d.highlight) return "#6c84d0"
//
//                    if (d.data.creationStackTrace)
//                        return "#FFB266"
//                    else return originalColor
//                });
//
//                fetch("coro-profiling-all.json")
//                    .then(response => response.json())
//                    .then(json => {
//                        let coro = json.find(function (coro) {
//                            return coro.id === d.data.id
//                        });
//
//                        let creationStackTrace = stacksFromList(coro.creationStackTrace, d.data.samples, (stackFrame) => {
//                            stackFrame["creationStackTrace"] = true
//                            return stackFrame
//                        })
//
//                        let currentCoroutineSampleStackTrace = stacksFromList(d.data.stacktrace.split(", "), d.data.samples, (stackFrame) => {
//                            stackFrame["creationStackTrace"] = false
//                            return stackFrame
//                        })
//
//                        let formattedStackTrace = appendStackTrace(creationStackTrace, currentCoroutineSampleStackTrace)
//
//                        d3.select("#stacktrace")
//                            .datum(formattedStackTrace)
//                            .call(coroutineStackFlameGraph);
//                    });
//            };
//
//            document.getElementById("form").addEventListener("submit", event => {
//                event.preventDefault();
//                search();
//            });
//
//            const flameGraph = flamegraph()
//                .width(1300)
//                .title("Coroutines dump")
//                .tooltip(true)
//                .onClick(onClick)
//            ;
//
//            const details = document.getElementById("details");
//            flameGraph.setDetailsElement(details);
//
//
//            let label = function (d) {
//                return "name: " + d.data.name + "\n" +
//                    "value: " + d.value + "\n" +
//                    "samples: " + d.data.samples + "\n" +
//                    "state: " + d.data.state + "\n" +
//                    (d.data.state === "RUNNING" ? "thread: " + d.data.thread + "\n": "")
//            }
//            flameGraph.label(label);
//
//            flameGraph.setColorMapper(coroutineStatesColorMapper);
//
//
//            d3.json("${flameJson.name}")
//                .then(data => {
//                    d3.select("#chart")
//                        .datum(data)
//                        .call(flameGraph);
//                }).catch(error => {
//                return console.warn(error);
//            });
//        </script>
//        </body>
//
//    """.trimIndent()
//    )

    htmlFile.writeText("""
        <head>
            <link rel="stylesheet" type="text/css"
                  href="https://cdn.jsdelivr.net/npm/d3-flame-graph@4.1.3/dist/d3-flamegraph.css"/>

            <link rel="stylesheet"
                  href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>

        </head>
        <body>

        <div class="container">
            <div class="header clearfix">
                <nav>
                    <div class="pull-right">
                        <form class="form-inline" id="form">
                            <a class="btn" href="javascript: resetZoom();">Reset zoom</a>
                            <a class="btn" href="javascript: clear();">Clear</a>
                            <div class="form-group">
                                <input type="text" class="form-control" id="term"/>
                            </div>
                            <a class="btn btn-primary" href="javascript: search();">Search</a>
                        </form>
                    </div>
                </nav>
                <h3 class="text-muted">Coroutines Profiler results</h3>
            </div>
        </div>
        <div id="chart" style="display: flex; justify-content: center; margin-bottom: 5%"></div>


        <div id="coroutineInfo">
            <h3 id="coroutineName" style="display: flex; justify-content: center"></h3>
            <div id="coroutineTimeLine" style="display: flex; justify-content: center"></div>
            <div id="stacktrace" style="display: flex; justify-content: center"></div>
        </div>

        <script type="text/javascript" src="https://d3js.org/d3.v7.js"></script>
        <script type="text/javascript"
                src="https://cdn.jsdelivr.net/npm/d3-flame-graph@4.1.3/dist/d3-flamegraph.min.js"></script>
        <script type="text/javascript">

            const search = () => {
                let term = document.getElementById("term").value;
                flameGraph.search(term);
                coroutineStackFlameGraph?.search(term);
            };

            const clear = () => {
                document.getElementById("term").value = "";
                flameGraph.clear();
                coroutineStackFlameGraph.clear();
            };
            const resetZoom = () => {
                coroutineStackFlameGraph = null
                coroutineTimeLineFlameGraph = null
                flameGraph.resetZoom();

                clearAllChildren(document.getElementById("stacktrace"))
                clearAllChildren(document.getElementById("coroutineTimeLine"))
                document.getElementById("coroutineTimeLine").innerHTML = ""
            };


            function stacksFromList(list, value, eachAction) {
                let current = eachAction({
                    name: list[0],
                    value: value,
                });

                let currentIdx = 1
                while (currentIdx < list.length) {
                    current = eachAction({
                        name: list[currentIdx++],
                        value: value,
                        children: [current]
                    })
                }

                return current
            }

            function appendStackTrace(to, what) {
                let current = to
                while (current.hasOwnProperty('children')) {
                    current = current.children[0]
                }
                current.children = [what]

                return to
            }

            function coroutineStatesColorMapper(d, originalColor) {
                if (d.highlight) return "#6c84d0"

                if (d.data.state === "CREATED")
                    return "#FFB266"
                if (d.data.state === "RUNNING")
                    return "#70ff66"
                if (d.data.state === "SUSPENDED")
                    return "#FF6666"

                return originalColor
            }

            let coroutineStackFlameGraph
            let coroutineTimeLineFlameGraph


            function deepFilterCoroutineSamples(list, filter) {
                let objsList = []

                function deepFilterCoroutineSamples(subList, filter, appropriates) {
                    subList.filter(filter).forEach(coro => appropriates.push(coro))
                    subList.forEach(coro => deepFilterCoroutineSamples(coro.children, filter, appropriates))
                }

                deepFilterCoroutineSamples(list, filter, objsList)

                return objsList
            }

            const clearAllChildren = parent => {
                let child = parent.lastElementChild
                while (child) {
                    parent.removeChild(child)
                    child = parent.lastElementChild
                }
            }

            const showCoroutineStateStacktrace = d => {
                clearAllChildren(document.getElementById("stacktrace"))

                if (d.data.name === "root")
                    return

                coroutineStackFlameGraph = flamegraph()
                    .title("Stacktrace for #" + d.data.id + " - " + d.data.kind + " at " + d.data.state)

                coroutineStackFlameGraph.setColorMapper(function (d, originalColor) {
                    if (d.highlight) return "#6c84d0"

                    if (d.data.creationStackTrace)
                        return "#FFB266"
                    else return originalColor
                });

                fetch("coro-profiling-all.json")
                    .then(response => response.json())
                    .then(json => {
                        let coro = json.find(function (coro) {
                            return coro.id === d.data.id
                        });

                        let creationStackTrace = stacksFromList(coro.creationStackTrace, d.data.samples, (stackFrame) => {
                            stackFrame["creationStackTrace"] = true
                            return stackFrame
                        })

                        let currentCoroutineSampleStackTrace = stacksFromList(d.data.stacktrace.split(", "), d.data.samples, (stackFrame) => {
                            stackFrame["creationStackTrace"] = false
                            return stackFrame
                        })

                        let formattedStackTrace = appendStackTrace(creationStackTrace, currentCoroutineSampleStackTrace)

                        d3.select("#stacktrace")
                            .datum(formattedStackTrace)
                            .call(coroutineStackFlameGraph);
                    });

            }

            const onClick = d => {
                clearAllChildren(document.getElementById("coroutineTimeLine"))
                clearAllChildren(document.getElementById("stacktrace"))
                document.getElementById("coroutineName").innerHTML = "";

                if (d.data.name === "root")
                    return

                document.getElementById("coroutineName").innerHTML = `Coroutine ${'$'}{d.data.name}`;

                coroutineTimeLineFlameGraph = flamegraph()
                    .onClick(showCoroutineStateStacktrace);

                fetch("${flameJson.name}")
                    .then(response => response.json())
                    .then(json => {
                        coroutineTimeLineFlameGraph.setColorMapper(coroutineStatesColorMapper);

                        let coroutineTimeLine = deepFilterCoroutineSamples(json.children, coro => coro.id === d.data.id).map(coro => {
                            coro.children = [];
                            coro.value = coro.samples;
                            coro.name = coro.state
                            return coro
                        });

                        let totalValueForCoroutine = coroutineTimeLine.reduce(
                            (previous, current) => previous + current.value, 0
                        )

                        d3.select("#coroutineTimeLine")
                            .datum(
                                {
                                    name: `coroutine ${'$'}{d.data.name}`,
                                    value: totalValueForCoroutine,
                                    children: coroutineTimeLine
                                }
                            )
                            .call(coroutineTimeLineFlameGraph);
                    })

                showCoroutineStateStacktrace(d);
            };

            document.getElementById("form").addEventListener("submit", event => {
                event.preventDefault();
                search();
            });

            const flameGraph = flamegraph()
                .width(1300)
                .title("Coroutines dump")
                .tooltip(true)
                .onClick(onClick)
            ;

            const details = document.getElementById("details");
            flameGraph.setDetailsElement(details);


            let label = function (d) {
                return "name: " + d.data.name + "\n" +
                    "value: " + d.value + "\n" +
                    "samples: " + d.data.samples + "\n" +
                    "state: " + d.data.state + "\n" +
                    (d.data.state === "RUNNING" ? "thread: " + d.data.thread + "\n": "")
            }
            flameGraph.label(label);

            flameGraph.setColorMapper(coroutineStatesColorMapper);

            d3.json("${flameJson.name}")
                .then(data => {
                    d3.select("#chart")
                        .datum(data)
                        .call(flameGraph);
                }).catch(error => {
                return console.warn(error);
            });
        </script>
        </body>

    """.trimIndent())


    return htmlFile
}
