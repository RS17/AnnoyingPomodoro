package com.github.rs17.annoyingpomodoro_desktop

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object DesktopEnvironment {
    val mediaPlayerRunning : JavaxMediaPlayer = JavaxMediaPlayer("ticktock2.wav")
    val mediaPlayerDone : JavaxMediaPlayer = JavaxMediaPlayer("done.wav")

    fun getProperties(): Properties {
        if (File("settings.xml").exists()) {
            val loadProps = Properties()
            loadProps.loadFromXML(FileInputStream("settings.xml"));
            return loadProps
        } else {
            return getDefaultProperties()
        }
    }

    fun getDefaultProperties() : Properties{
        val newProps = Properties();
        newProps.setProperty("workSeconds", (25 * 60).toString())
        newProps.setProperty("shortBreakSeconds", (5 * 60).toString())
        newProps.setProperty("longBreakSeconds", (25 * 60).toString())
        newProps.setProperty("tickingSound", (true).toString())
        newProps.setProperty("shortBreaksUntilLong", 4.toString())
        newProps.setProperty("hasDancer", (true).toString())
        val destination = FileOutputStream("settings.xml")
        newProps.storeToXML(destination, "")
        println("Created new settings file" + System.getProperty("user.dir") + "/settings.xml")
        return newProps
    }

}