package com.github.rs17.annoyingpomodoro

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.github.rs17.annoyingpomodoro_lib.TimerRun
import com.github.rs17.annoyingpomodoro_lib.UniversalState

class AndroidCallListener(appState_i: UniversalState) : PhoneStateListener() {
    val appState: UniversalState = appState_i
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        if( appState.pauseOnCall && TelephonyManager.CALL_STATE_OFFHOOK == state ) {
            if(appState.currentTimerRun?.timerState == TimerRun.TimerState.running) {
                appState.currentTimerRun?.pause()
            } else {
                // if already stopped still want to silence on the call
                appState.mainUI.killRunningPlayer()
            }
            super.onCallStateChanged(state, phoneNumber)
        }
    }

}