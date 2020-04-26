package com.github.rs17.annoyingpomodoro

import com.github.rs17.annoyingpomodoro.SettingsActivity.SettingsFragment.Companion.pomodoroDurationId

class TimerRunWork(appState: UniversalState, activity: MainUI) : TimerRun(activity, appState, R.raw.ticktock2, R.raw.done) {
    override val nextRun: TimerRun by lazy{
        // do long break if at limit
        if(appState.pomodorosSinceLongBreak < appState.shortBreaksUntilLong) TimerRunShortBreak(appState, activity) else TimerRunLongBreak(appState, activity)
    }
    override val finishMessage: String = "*** Pomodoro finished ***"

    override fun handleFinish(){
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
        mainUI.displayTime(appState.workMillis)
    }

    override fun update(updatedKey: String){
        // make player start/stop on settings update based on ticking sound checkbox (hasTick)
        if(appState.hasTick && timerState == TimerState.running) mainUI.startRunningPlayer(runPlayerResource) else mainUI.killRunningPlayer()
        if(updatedKey == pomodoroDurationId){
            reset()
        }
    }
}