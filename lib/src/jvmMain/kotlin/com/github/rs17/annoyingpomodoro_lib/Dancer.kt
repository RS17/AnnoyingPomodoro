package com.github.rs17.annoyingpomodoro_lib

object Dancer {
    fun dance(appState: UniversalState): String{
        val time = appState.timerMillisRemaining
        if( appState.hasDancer){
            return when(appState.currentTimerRun!!.timerState){
                TimerRun.TimerState.running -> if (((time + 50) / 500) % 2 == 1L) "┏(･o･)┛♪" else "♪┗(･o･)┓"
                TimerRun.TimerState.stopped -> "┏(･_･)┓"
            }
        } else {
            return ""
        }
    }

    fun sleep(appState: UniversalState): String{
        val time = appState.timerMillisRemaining
        return if( appState.hasDancer){
            if ((time / 1000) % 2==1L) "(-_-)z..z" else "(-_-)..z."
        } else {
            ""
        }
    }

    fun wait(appState: UniversalState): String{
        return if( appState.hasDancer){
            "( ≧o≦)"
        } else {
            ""
        }
    }
}