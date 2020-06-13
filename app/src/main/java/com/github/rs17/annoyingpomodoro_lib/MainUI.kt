package com.github.rs17.annoyingpomodoro_lib

import java.io.File

interface MainUI {
    // includes what every UI must do to support timer classes
    fun displayTime(time: Long)
    fun getFilesDirectory(): File
    fun handleFinish(finishMessage: String)
    fun setOnStart(f:()->Unit)
    fun setToResume()
    fun startRunningPlayer(runPlayerResource: Int)
    fun killRunningPlayer()
    fun switchToStop()
    fun switchToStart()
    fun update()
    fun update(millisUntilFinished: Long)
}