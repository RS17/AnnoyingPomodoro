package com.github.rs17.annoyingpomodoro_android

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

abstract class AndroidDoNotKillMeService : Service(){
    /**
     * This service exists solely to stop the Android "feature" that on seemingly random occasions kills apps
     * that aren't in the foreground with no log or error message whatsoever, leaving the developer to wonder
     * whether there is some strange conspiracy afoot that is trying to drive him insane
     * see https://developer.android.com/about/versions/oreo/background for details
     */
    class LocalBinder : Binder(){
    }

    private val mBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        return result
    }
}