package com.github.rs17.annoyingpomodoro_lib

import java.util.*

// timer run includes both the start and the stop of the timer
// this should handle everything to do with running the timer that is *not* UI
abstract class TimerRun(mainUI_i: MainUI, appState_i: UniversalState, runPlayerResource_i : Any, stopPlayerResource_i : Any ) {
    abstract val nextRun : TimerRun
    abstract val finishMessage: String
    abstract val skipMessage: String


    private val currentTimer: Timer = Timer()
    val appState : UniversalState = appState_i
    val pomodoroLogger : PomodoroLogger =
        PomodoroLogger(appState.filesDir)
    val mainUI : MainUI = mainUI_i
    //The timer needs a state because the user should only hear ticking if in playing state.  All state is tied to UI.
    var timerState : TimerState =
        TimerState.stopped
    val runPlayerResource = runPlayerResource_i
    val stopPlayerResource = stopPlayerResource_i

    var timerTask :TimerTask? = null

    enum class TimerState{
        running,
        stopped
        //Stop! TimerState solely represents whether currentTimer is ticking or not.
    }

    // do whatever is needed to attach the timer to the UI here
    open fun prepare(){
        if( appState.currentTimerRun != this) {
            appState.currentTimerRun = this
        }
    }

    fun run(remainMillis : Long, runMessage: String, startPlayer: Boolean) : TimerRun { // pass in millis to allow resuming
        mainUI.switchToStop()

        // updates UI every 100 ms - updates time remaining and checks if done
        val startDate = Date()
        timerTask = object: TimerTask() {
            override fun run() {
                appState.update(remainMillis - (Date().time - startDate.time))
                if (appState.timerMillisRemaining <= 0) {
                    timerTask?.cancel()
                    handleFinish()
                }
            }
        }
        currentTimer.scheduleAtFixedRate( timerTask, startDate, 100)
        timerState =
            TimerState.running

        if(startPlayer) {
            mainUI.startRunningPlayer(runPlayerResource)
        }
        pomodoroLogger.addToLog(runMessage + " " + Calendar.getInstance().time)
        return this
    }

    open fun handleFinish(){
        timerState =
            TimerState.stopped
        mainUI.killRunningPlayer()
        mainUI.startRunningPlayer(stopPlayerResource)
        appState.endedTimerRun = this
        appState.currentTimerRun = null
        nextRun.prepare()
        pomodoroLogger.addToLog( finishMessage + " " + Calendar.getInstance().time)
        mainUI.handleFinish(finishMessage)
    }

    fun pause(){
        if(timerState == TimerState.running) {
            timerTask?.cancel()
            timerState =
                TimerState.stopped
            mainUI.killRunningPlayer()
            mainUI.setOnStart {
                appState.currentTimerRun!!.run(
                    appState.timerMillisRemaining,
                    StringConst.POM_RESUMED.message,
                    appState.hasTick
                )
            }
            mainUI.setToResume()
            pomodoroLogger.addToLog(StringConst.PAUSED.message + Calendar.getInstance().time)
        }
    }

    fun kill(){
        mainUI.killRunningPlayer()
        currentTimer.cancel()
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
        timerTask?.cancel()
        mainUI.killRunningPlayer()
        prepare()
    }

    fun skip(){
        timerState =
            TimerState.stopped
        mainUI.killRunningPlayer()
        mainUI.startRunningPlayer(stopPlayerResource)
        appState.endedTimerRun = this
        appState.currentTimerRun = null
        nextRun.prepare()
        pomodoroLogger.addToLog( skipMessage + " " + Calendar.getInstance().time)
        mainUI.handleFinish(skipMessage)
    }

    abstract fun dancerMessage(): String

    // color should be parseable by Android: https://developer.android.com/reference/android/graphics/Color#parseColor(java.lang.String)
    abstract fun getColorCode(): String


}