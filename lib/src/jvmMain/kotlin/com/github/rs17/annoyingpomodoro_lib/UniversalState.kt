package com.github.rs17.annoyingpomodoro_lib

import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDate
import java.util.*

class UniversalState(mainUI_i: MainUI){
    // This holds any state that should be consistent across multiple UIs
    // Goal is to avoid having anything here if possible, without going nuts trying to avoid having state

    // timer that just ended - need to keep this around for a bit because it might be beeping
    var endedTimerRun : TimerRun? = null
    var timerMillisRemaining: Long = 0L
    var pomodorosThisSession = 0
    var pomodorosSinceLongBreak = 0
    var pomodorosToday = 0

    var workMillis = 1500000L
    var shortBreakMillis = 300000L
    var longBreakMillis = 1500000L
    var hasTick = true
    var shortBreaksUntilLong = 4
    var pauseOnCall = true
    var hasDancer = true
    var lastDate = LocalDate.of(1980,1,1)

    val mainUI = mainUI_i
    val filesDir = mainUI.getFilesDirectory()
    val pomodoroLogger : PomodoroLogger =
        PomodoroLogger(filesDir)

    val storagePath by lazy{ filesDir.canonicalPath + File.separator + "stats"}

    class StoredUniversalState(val pomodorosToday: Int, val lastDate: LocalDate): Serializable


    // timer currently displaying time
    var currentTimerRun : TimerRun? = null
        set(value){
            check(currentTimerRun==null || value == null){
                throw Exception("Timer already exists!  Must .kill() previous timer before setting to new.")
            }
            field = value
        }

    fun update(millisUntilFinished: Long) {
        mainUI.displayTime(millisUntilFinished)
        timerMillisRemaining = millisUntilFinished
        if(lastDate.isBefore(LocalDate.now())){
            handleOvernight();
        }
    }

    fun addPomodoro(){
        pomodorosThisSession++ //for superclass
        pomodorosToday++
        mainUI.update()
        saveData()
    }

    fun onCreate(){
        pomodoroLogger.addToLog("Program start " + Calendar.getInstance().time)
        loadData()
    }

    fun onDestroy(){
        currentTimerRun?.abort()
        pomodoroLogger.addToLog("Program exit " + Calendar.getInstance().time)
        saveData()
    }

    fun handleOvernight(){
        pomodorosToday = 0
        lastDate = LocalDate.now()
        saveData();
    }

    fun saveData(){
        val data = StoredUniversalState(this.pomodorosToday, this.lastDate);
        val bos = ByteArrayOutputStream()
        try {
            val out = ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();
            val bytes = bos.toByteArray()
            File(storagePath).writeBytes(bytes)
        } finally {
            try {
                bos.close();
            } catch (ex: java.lang.Exception) {
                pomodoroLogger.addToLog("ERROR: exception on save to: " + storagePath + "\n" + ex)
            }
        }
    }

    fun loadData(){
        if(Files.exists(Paths.get(storagePath))) {
            val bytes = Files.readAllBytes(Paths.get(storagePath))
            val inputStream = ObjectInputStream(ByteArrayInputStream(bytes))
            val inResult = try {
                inputStream.readObject()
            } catch (ex: java.lang.Exception) {
                pomodoroLogger.addToLog("ERROR: Failed to read stored stats file at: " + storagePath + "\n" + ex)
            } finally {
                inputStream.close()
            }
            val storedState: StoredUniversalState = inResult as StoredUniversalState
            pomodoroLogger.addToLog("Retrieved data from: " + storedState.lastDate)
            lastDate = storedState.lastDate
            pomodorosToday = if(storedState.lastDate.isBefore(LocalDate.now().atStartOfDay().toLocalDate())) 0 else storedState.pomodorosToday
        } else {
            pomodoroLogger.addToLog("Warning, failed to get data from: " + storagePath)
            lastDate = LocalDate.now()
            pomodorosToday = 0;
        }
    }

}