package com.github.rs17.annoyingpomodoro

import java.io.File

class PomodoroLogger(filesDir: File) {

    val logFilePath by lazy{ filesDir.canonicalPath + File.separator + "pomodoroLog"}

    fun addToLog(str: String){
        val logFile: File = getLogFile()
        if( !logFile.exists()){
            logFile.createNewFile()
        }
        // reduce log size if too big
        if(logFile.length() > 100000){
            val logList: List<String>  = logFile.readLines().map{lline-> (lline+"\n")}
                .takeLast(1000)
                .reversed()
            logFile.writeText("List truncated here for size!\n".plus(logList.toString()))
        }


        logFile.appendText(str + "\n")
    }


    fun getLogFile():File{
        return File(logFilePath);
    }

}