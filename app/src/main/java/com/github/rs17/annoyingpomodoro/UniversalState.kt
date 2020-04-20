package com.github.rs17.annoyingpomodoro

import java.util.*

class UniversalState(mainUI_i: MainUI) {
    // This holds any state that should be consistent across multiple UIs
    // Goal is to avoid having anything here if possible, without going nuts trying to avoid having state

    // timer that just ended - need to keep this around for a bit because it might be beeping
    var endedTimerRun : TimerRun? = null
    var timerMillisRemaining: Long = 0L
    var pomodorosThisSession = 0
    var pomodorosSinceLongBreak = 0

    var workMillis = 1500000L
    var shortBreakMillis = 300000L
    var longBreakMillis = 1500000L
    var hasTick = true
    var shortBreaksUntilLong = 4
    val mainUI = mainUI_i
    val filesDir = mainUI.getFilesDirectory()
    val pomodoroLogger : PomodoroLogger = PomodoroLogger(filesDir)

    // timer currently displaying time
    var currentTimerRun : TimerRun? = null
        set(value){
            check(currentTimerRun==null || value == null){
                throw Exception("Timer already exists!  Must .kill() previous timer before setting to new.")
            }
            field = value
        }

    fun update(millisUntilFinished: Long): Unit {
        mainUI.displayTime(millisUntilFinished)
        timerMillisRemaining = millisUntilFinished
    }

    fun addPomodoro(){
        pomodorosThisSession++ //for superclass
        mainUI.update()
    }

    fun onCreate(){

        pomodoroLogger.addToLog("Program start " + Calendar.getInstance().time)
    }

    fun onDestroy(){
        currentTimerRun?.abort()
        pomodoroLogger.addToLog("Program exit " + Calendar.getInstance().time)
    }

}