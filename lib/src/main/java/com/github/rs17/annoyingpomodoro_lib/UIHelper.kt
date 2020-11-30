package com.github.rs17.annoyingpomodoro_lib

import java.net.URL
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object UIHelper {

    // String functions
    fun timeFormatRemain(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) - minutes * 60
        return minutes.toString() + ":" + String.format("%02d", seconds)
    }

    // String functions
    fun timeFormatTOD(time: LocalTime): String {
        return String.format("%02d", time.hour) + ":" + String.format("%02d", time.minute) + ":" + String.format("%02d", time.second)
    }

    fun mmss2millis(str: String?) : Long {
        val arr = str!!.split(":")
        return arr.get(0).toLong() * 60000 + arr.get(1).toLong() * 1000
    }
}