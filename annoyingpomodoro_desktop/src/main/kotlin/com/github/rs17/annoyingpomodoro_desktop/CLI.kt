package com.github.rs17.annoyingpomodoro_desktop

import com.github.rs17.annoyingpomodoro_lib.*
import java.io.File
import java.lang.IllegalStateException
import java.time.LocalTime
import java.util.*

class CLI: MainUI {
    val appState by lazy{
        UniversalState(this)
    }


    // no idea why neither Java nor Kotlin has no easier way to load a resource file than this.
    override val doneResource: Int = 2
    override val tickTockResource: Int = 1
    override val shortBreakDurationId: String = ""
    override val longBreakDurationId: String = ""
    override val pomodoroDurationId: String = ""

    // need list to keep threads from being garbage collected I think.
    // This would be memory leak but probably not a big deal.
    val pauseThreads = mutableListOf<Thread>()
    val startThreads = mutableListOf<Thread>()
    val cli = CLIIO()

    var onClick = {}
    var enterMessage = "";

    init{
        val properties: Properties = DesktopEnvironment.getProperties()
        try {
            appState.onCreate()
            appState.workMillis = properties.getProperty("workSeconds").toLong()*1000
            appState.shortBreakMillis = properties.getProperty("shortBreakSeconds").toLong()*1000
            appState.longBreakMillis = properties.getProperty("longBreakSeconds").toLong()*1000
            appState.hasTick = properties.getProperty("tickingSound").toBoolean()
            appState.shortBreaksUntilLong = properties.getProperty("shortBreaksUntilLong").toInt()
            appState.hasDancer = properties.getProperty("hasDancer").toBoolean()
        } catch(ex: Exception){
            when(ex){
                is IllegalStateException -> throw IllegalStateException("Error reading properties.  Please check that settings.xml contains all properties: " + ex.message)
            }
        }
        printWelcome()
        TimerRunWork(appState, this).prepare();
        displayTime(appState.timerMillisRemaining)
    }

    private fun printWelcome(){
        val logo = "          \n" +
                "                                       ....                                     \n" +
                "                              .  .  ...     ..........................          \n" +
                "                            ..  ....                                  ..        \n" +
                "                         . .   ..                                       ..      \n" +
                "                       ....        /// ///  ////////////    //                .    \n" +
                "              ........ ..         /// // ///////////     ///                 .  \n" +
                "              .                   ////////////////  //////                   .. \n" +
                "               .                  ////////////////////                       .. \n" +
                "                .                #/////////////###################           .  \n" +
                "  ......         .       ###########################################         .. \n" +
                "     .      ...  .    ###############################################       ..  \n" +
                "       .              ##############################################        .   \n" +
                "        ..          ###############################################        .    \n" +
                "          .         ##############################################         .    \n" +
                "            ..   .   ############################################          .    \n" +
                "               .  .   #########################################          .     \n" +
                "                   .   ######################################           ..      \n" +
                "                    ..   ##  #############################      .... ..         \n" +
                "                      .    ###  ######################            ..            \n" +
                "                         ..            ...       ....       ...                        \n" +
                "                           .    ...        .   ..          ...                          \n"

        cli.printColorMulti(logo, '#', '/', '.')
        println()
        cli.printColorLine("Welcome to Annoying Pomodoro!", cli.colorMap["green"]!!)
        println()
    }

    // Main UI stuff
    override fun displayTime(time: Long){
        val dstr: String = appState.currentTimerRun!!.dancerMessage()
        cli.printOverwriteableColor(UIHelper.timeFormatRemain(time) + "      " + enterMessage + "   " + dstr, cli.colorMap[appState.currentTimerRun?.getColorCode() ?: "white"]!!)
    }
    override fun getFilesDirectory(): File {return File(".")}
    override fun handleFinish(finishMessage: String){
        synchronized(pauseThreads) {
            pauseThreads.forEach { t -> t.interrupt() }
        }
        cli.printColorLine(UIHelper.timeFormatTOD(LocalTime.now()) + "   " + finishMessage, cli.colorMap["green"]!!)
        println("                                                   ")
        displayTime(appState.timerMillisRemaining)
    }

    override fun setOnStart(f:()->Unit){
        onClick = f
    }
    override fun setToResume(){
        switchToStart()
    }
    override fun startRunningPlayer(runPlayerResource: Any){
        // since Android has int values for resources we end up with this weird int matching as an adapter
        when(runPlayerResource) {
            doneResource -> DesktopEnvironment.mediaPlayerDone.start()
            tickTockResource -> DesktopEnvironment.mediaPlayerRunning.start()
        }
    }
    override fun killRunningPlayer(){
        DesktopEnvironment.mediaPlayerRunning.stop()
        DesktopEnvironment.mediaPlayerDone.stop()
    }
    override fun switchToStop(){
        enterMessage = "Press enter to pause"
        displayTime(appState.timerMillisRemaining)
        val pauseThread = Thread({
            try {
                while (cli.checkOutput() == null) {
                    Thread.sleep(100)
                }
                cli.previousLine()
                appState.currentTimerRun?.pause() ?: throw Exception("currentTimerRun is null")
                enterMessage = "Paused"
            } catch( ex: InterruptedException) {
                // no catch, interrupt happens when pomodoro ends
            } finally {
                synchronized(pauseThreads) {
                    pauseThreads.clear()
                }
            }
        }, "pauseThread")
        pauseThread.start()
        synchronized(pauseThreads){
            pauseThreads.add(pauseThread)
        }
    }
    override fun switchToStart(){
        val startThread = Thread({
            val type = when(appState.currentTimerRun){
                is TimerRunWork -> "pomodoro"
                is TimerRunLongBreak -> "long break"
                is TimerRunShortBreak -> "short break"
                else -> throw Exception("unknown timer run")
            }
            enterMessage = "Press enter to begin " + type
            displayTime(appState.timerMillisRemaining)
            while(cli.checkOutput() == null) {Thread.sleep(100)}
            cli.previousLine() //move line up here
            onClick()
            synchronized(startThreads) {
                startThreads.clear()
            }
        }, "startThread")
        startThread.start()
        synchronized(startThreads) {
            startThreads.add(startThread)
        }
    }

    override fun update(){
        cli.clearLine()
        println("   " + StringConst.POMS_THIS_SESSION.message + appState.pomodorosThisSession);
        println("   " + StringConst.POMS_TODAY.message + appState.pomodorosToday)
        println("   " + StringConst.POMS_TILL_LONG_BREAK.message + (appState.shortBreaksUntilLong - appState.pomodorosSinceLongBreak))
        displayTime(appState.timerMillisRemaining)
    }
}