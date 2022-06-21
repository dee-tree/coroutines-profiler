package kotlinx.coroutines.profiler.show.ui

/*
class SelectedCoroutineInfo {
    private val scope = MainScope()

    val fc = FC<SelectedCoroutineProps> { props ->

        var coroutineId: Long by useState<Long>(-1)
        var coroutineName: String by useState<String>("")
        var coroutineSuspensionTimes: Int by useState(0)
        var coroutineSuspensionFramesCount: Int by useState(-1)
        var coroutineRunningFramesCount: Int by useState(-1)
        var coroutineCreatedFramesCount: Int by useState(-1)

        useEffect {
            scope.launch {
                coroutineId = props.selectedCoroutineId
                val coroutineInfo = api.getCoroutineReport(props.selectedCoroutineId)

                coroutineName = coroutineInfo.name

            }
        }

        div {
            id = "coroutineInfoBox"
            css {
                borderRadius = 2.em
                padding = 2.em
                margin = 2.em
            }

            h5 {
                +"$coroutineId | $coroutineName"
            }

            + "Suspended $coroutineSuspensionTimes times"
            br()
            + "Sampled $coroutineSuspensionFramesCount times at SUSPENDED state"

            br()
            + "Sampled $coroutineRunningFramesCount times at RUNNING state"

            br()
            + "Sampled $coroutineCreatedFramesCount times at CREATED state"
        }

        */
/*fun showCoroutineInfo(coroutineId: Long) {
            scope.launch {
                api.getCoroutineReport(coroutineId)
            }
        }

        fun hide() {

        }*//*

    }
}

external interface SelectedCoroutineProps : Props {
    var selectedCoroutineId: Long
}*/
