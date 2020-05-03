package com.github.rs17.annoyingpomodoro

import java.io.File

class PomodoroLogger(filesDir: File) {

    val logFilePath by lazy{ filesDir.canonicalPath + File.separator + "pomodoroLog"}

    fun addToLog(str: String){
        // remember: on display end of log file is at top to ensure most recent activity is visible
        val logFile: File = getLogFile()
        if( !logFile.exists()){
            logFile.createNewFile()
        }

        val maxlen = 100000
        val truncateLines = 1000

        // truncate log if too big - first entries are oldest so takeLast and put truncate message first
        if(logFile.length() > maxlen){
            val logList: List<String>  = logFile.readLines().map{lline-> (lline+"\n")}
                .takeLast(truncateLines)
            logFile.writeText("List truncated here for size!\n".plus(logList.joinToString("")))
        }

        logFile.appendText(str + "\n")
    }


    fun getLogFile():File{
        return File(logFilePath)
    }

}