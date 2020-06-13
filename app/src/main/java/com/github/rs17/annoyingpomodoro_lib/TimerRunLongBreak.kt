package com.github.rs17.annoyingpomodoro_lib

import com.github.rs17.annoyingpomodoro.R
import com.github.rs17.annoyingpomodoro_lib.SettingsActivity.SettingsFragment.Companion.longBreakDurationId

class TimerRunLongBreak(appState: UniversalState, activity: MainUI) : TimerRun(activity, appState, -1,
    R.raw.done
) {
    override val nextRun: TimerRun by lazy{
        TimerRunWork(
            appState,
            activity
        )
    }
    override val finishMessage: String = "Long break finished"

    override fun prepare() {
        super.prepare()
        mainUI.switchToStart()
        mainUI.setOnStart {
            appState.endedTimerRun?.kill()
            this.run(appState.longBreakMillis, "Long break started", false)
        }
        mainUI.displayTime(appState.longBreakMillis)
        appState.pomodorosSinceLongBreak = 0
    }

    override fun update(updatedKey: String){
        if( updatedKey == longBreakDurationId){
            reset()
        }
    }

}