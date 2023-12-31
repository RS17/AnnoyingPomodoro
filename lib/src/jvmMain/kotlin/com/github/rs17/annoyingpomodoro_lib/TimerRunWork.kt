package com.github.rs17.annoyingpomodoro_lib
class TimerRunWork(appState: UniversalState, activity: MainUI) : TimerRun(activity, appState,
    activity.tickTockResource,
    activity.doneResource
) {
    val durationId = mainUI.pomodoroDurationId
    override val nextRun: TimerRun by lazy{
        // do long break if at limit
        if(appState.pomodorosSinceLongBreak < appState.shortBreaksUntilLong) TimerRunShortBreak(
            appState,
            activity
        ) else TimerRunLongBreak(
            appState,
            activity
        )
    }
    override val finishMessage: String = "*** Pomodoro finished ***"
    override val skipMessage: String = "Pomodoro skipped (???)"
    override val hasSkip = false;

    override fun handleFinish(){
        //TODO: There is very rare bug here somewhere in which pomodoros end in rapid succession with no start logged until
        // start break.  Only observed on android once.  Pomodoros since long break increments but long break never is
        // triggered or any other change in state.  Likely timer task in the calling super.run method is not getting cancelled
        // and continues to trigger this.
        appState.pomodorosSinceLongBreak++
        super.handleFinish()
        appState.addPomodoro()
    }

    override fun prepare() {
        super.prepare()
        mainUI.switchToStart()
        mainUI.setOnStart {
            appState.endedTimerRun?.kill()
            this.run(appState.workMillis, "Pomodoro started", appState.hasTick)
        }
        appState.timerMillisRemaining = appState.workMillis
        mainUI.displayTime(appState.workMillis)
    }

    override fun update(updatedKey: String){
        // make player start/stop on settings update based on ticking sound checkbox (hasTick)
        if(appState.hasTick && timerState == TimerState.running) mainUI.startRunningPlayer(runPlayerResource) else mainUI.killRunningPlayer()
        if(updatedKey == durationId){
            reset()
        }
    }

    override fun dancerMessage(): String {
        return if(appState.timerMillisRemaining < appState.workMillis) Dancer.dance(appState) else Dancer.wait(appState)
    }
    override fun getColorCode(): String {
        if(timerState.equals(TimerState.running)){
            return "green"
        } else {
            return "red"
        }
    }
}