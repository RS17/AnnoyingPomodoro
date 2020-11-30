package com.github.rs17.annoyingpomodoro_desktop

import java.io.File

class SwingUI : com.github.rs17.annoyingpomodoro_lib.MainUI {

    // Not built yet!

    // Main UI stuff
    override val doneResource: Int =-1
    override val tickTockResource: Int = -1
    override val shortBreakDurationId: String = ""
    override val longBreakDurationId: String = ""
    override val pomodoroDurationId: String = ""

    override fun displayTime(time: Long){}
    override fun getFilesDirectory(): File{return File("");}
    override fun handleFinish(finishMessage: String){}
    override fun setOnStart(f:()->Unit){}
    override fun setToResume(){}
    override fun startRunningPlayer(runPlayerResource: Any){}
    override fun killRunningPlayer(){}
    override fun switchToStop(){}
    override fun switchToStart(){}
    override fun update(){}
}