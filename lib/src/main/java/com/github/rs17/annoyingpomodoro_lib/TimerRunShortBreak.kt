package com.github.rs17.annoyingpomodoro_lib

class TimerRunShortBreak(appState: UniversalState, mainUI_i: MainUI) :
    TimerRun(mainUI_i, appState,-1,
    mainUI_i.doneResource
) {
    val durationId = mainUI.shortBreakDurationId
    override val nextRun: TimerRun by lazy{
        TimerRunWork(
            appState,
            mainUI_i
        )
    }
    override val finishMessage: String = "Short break finished"

    override fun prepare() {
        super.prepare()
        mainUI.setOnStart {
            appState.endedTimerRun?.kill()
            this.run(appState.shortBreakMillis, "Short break started", false)
        }
        mainUI.switchToStart()
        appState.timerMillisRemaining = appState.shortBreakMillis
        mainUI.displayTime(appState.shortBreakMillis)
    }

    override fun update(updatedKey: String){
        if( updatedKey == durationId){
            reset()
        }
    }

    override fun dancerMessage(): String {
        return if(appState.timerMillisRemaining < appState.shortBreakMillis) Dancer.sleep(appState) else Dancer.wait(appState)
    }
}