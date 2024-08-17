package com.github.rs17.annoyingpomodoro_lib

import java.io.File

interface MainUI {
    val tickTockResource: Any  //Any because Android expects resources to be ints and normal Java expects them to be strings
    val doneResource: Any
    val longBreakDurationId: String
    val shortBreakDurationId: String
    val pomodoroDurationId: String


    // includes what every UI must do to support timer classes
    fun displayTime(time: Long)
    fun getFilesDirectory(): File
    fun handleFinish(finishMessage: String)
    fun killRunningPlayer()
    fun setOnStart(f:()->Unit)
    fun setToResume()
    fun startRunningPlayer(playerResource: Any)
    fun skipBreak()
    fun switchToStop()
    fun switchToStart()
    fun update()
}