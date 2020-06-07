package com.github.rs17.annoyingpomodoro

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class AndroidCallListener(appState_i: UniversalState) : PhoneStateListener() {
    val appState:UniversalState = appState_i
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        if( appState.pauseOnCall && TelephonyManager.CALL_STATE_OFFHOOK == state ) {
            appState.currentTimerRun?.pause()
            super.onCallStateChanged(state, phoneNumber)
        }
    }

}