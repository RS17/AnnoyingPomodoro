package com.github.rs17.annoyingpomodoro_lib

class TimerRunLongBreak(appState: UniversalState, mainUI_i: MainUI) : TimerRun(mainUI_i, appState, -1,
    mainUI_i.doneResource
) {
    val durationId = mainUI.longBreakDurationId

    override val nextRun: TimerRun by lazy{
        TimerRunWork(
            appState,
            mainUI_i
        )
    }
    override val finishMessage: String = "Long break finished"
    override val skipMessage: String = "Short break skipped"
    override val hasSkip = true;

    override fun prepare() {
        super.prepare()
        mainUI.setOnStart {
            appState.endedTimerRun?.kill()
            this.run(appState.longBreakMillis, "Long break started", false)
        }
        mainUI.switchToStart()
        appState.timerMillisRemaining = appState.longBreakMillis
        mainUI.displayTime(appState.longBreakMillis)
        appState.pomodorosSinceLongBreak = 0
    }

    override fun update(updatedKey: String){
        if( updatedKey == durationId){
            reset()
        }
    }

    override fun dancerMessage(): String {
        return if(appState.timerMillisRemaining < appState.longBreakMillis) Dancer.sleep(appState) else Dancer.wait(appState)
    }

    override fun getColorCode(): String {
        if (timerState.equals(TimerState.running)) {
            return "white"
        } else {
            return "yellow"
        }
    }

}