package com.github.rs17.annoyingpomodoro

import android.os.CountDownTimer
import java.util.*

// timer run includes both the start and the stop of the timer
// this should handle everything to do with running the timer - sound, run state, etc.
// The problem is that UI is fundamentally stateful, so how do we do this functionally?
abstract class TimerRun(mainUI_i: MainUI, appState_i: UniversalState, runPlayerResource_i : Int, stopPlayerResource_i : Int ) {
    abstract val nextRun : TimerRun
    abstract val finishMessage: String

    var currentTimer: CountDownTimer? = null
    val appState : UniversalState = appState_i
    val pomodoroLogger : PomodoroLogger = PomodoroLogger(appState.filesDir)
    val mainUI : MainUI = mainUI_i
    //The timer needs a state because the user should only hear ticking if in playing state.  All state is tied to UI.
    var timerState : TimerState = TimerState.stopped
    val runPlayerResource = runPlayerResource_i
    val stopPlayerResource = stopPlayerResource_i

    enum class TimerState{
        running,
        stopped
        //Stop! TimerState solely represents whether currentTimer is ticking or not.
    }

    // do whatever is needed to attach the timer to the UI here
    open fun prepare(){
        if( appState.currentTimerRun != this) {
            appState.currentTimerRun = this;
        }
    }

    fun run(remainMillis : Long, runMessage: String, startPlayer: Boolean) : TimerRun{ // pass in millis to allow resuming
        mainUI.switchToStop()
        currentTimer = object: CountDownTimer(remainMillis, 100) {

            override fun onTick(millisUntilFinished: Long) {
                appState.timerMillisRemaining = millisUntilFinished
                appState.update(millisUntilFinished)
            }

            override fun onFinish() {
                handleFinish()
            }
        }

        if(startPlayer) {
            mainUI.startRunningPlayer(runPlayerResource)
        }
        currentTimer!!.start()
        timerState = TimerState.running
        pomodoroLogger.addToLog(runMessage + " " + Calendar.getInstance().time)
        return this
    }

    open fun handleFinish(){
        timerState = TimerState.stopped
        mainUI.killRunningPlayer()
        mainUI.startRunningPlayer(stopPlayerResource)
        appState.endedTimerRun = this
        appState.currentTimerRun = null
        nextRun.prepare()
        pomodoroLogger.addToLog( finishMessage + " " + Calendar.getInstance().time)
        mainUI.handleFinish(finishMessage)
    }

    fun pause(){
        currentTimer?.cancel() // may be null if timed right at 0?
        timerState = TimerState.stopped
        mainUI.killRunningPlayer()
        mainUI.setToResume()
        mainUI.setOnStart {
            appState.currentTimerRun!!.run(appState.timerMillisRemaining, StringConst.POM_RESUMED.message, appState.hasTick)
        }
        pomodoroLogger.addToLog(StringConst.PAUSED.message + Calendar.getInstance().time)
    }

    fun kill(){
        mainUI.killRunningPlayer()
        currentTimer?.cancel()
        if( appState.currentTimerRun == this){
            appState.currentTimerRun = null
        }
    }

    fun abort(){
        pomodoroLogger.addToLog("Run aborted")
        kill()
    }

    open fun update(updatedKey: String){
        // to be overridden as needed
    }

    fun reset(){
        currentTimer?.cancel()
        mainUI.killRunningPlayer()
        prepare()
    }

}