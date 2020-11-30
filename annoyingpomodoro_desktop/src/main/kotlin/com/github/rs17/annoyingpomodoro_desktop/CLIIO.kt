package com.github.rs17.annoyingpomodoro_desktop

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class CLIIO {
    // special class to deal with annoyances when reading from CLI
    // simply stores every user input in a list that can be retrieved
    // so the application can ask "Did the user just hit enter (or type something and hit enter)?"  And this can answer.
    var bufferedReader: BufferedReader = BufferedReader(InputStreamReader(System.`in`))
    // this will take a resource from CLI and trigger it on setting user input

    private val output = mutableListOf<String>()

    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"
    val ANSI_RESET = "\u001B[0m"
    val ANSI_YELLOW = "\u001B[93m"

    val cliThread = Thread({
        while(true) {
            val str = readLine() // for some reason this thing behaves completely unpredictably
            if (str != null) {
                synchronized(output) {
                    output.add(str)
                }
            }
            Thread.sleep(100)
        }
        // should never get to bottom
    }, "cliThread")

    init {
        cliThread.start()
    }

    fun checkOutput(): String?{
        if (output.size > 0) {
            if(output.size > 10){ println("WARNING: large output. Do you need to clear it?")}
            synchronized(output) {
                return output.removeAt(0)
            }
        }
        return null;
    }

    fun clearLine(){
       print("\r                                                                     \r")
    }
    fun printOverwriteable(s: String){
        clearLine()
        printOut(s + "\r")
    }
    fun printOut(s: String){
        print("   " + s)
    }
    fun printColorLine(s: String, color: String){
        clearLine()
        printOut(color + s + ANSI_RESET)
    }
    fun printColorMulti(s: String, redChar: Char, greenChar: Char, yellowChar: Char){
        // not exactly efficient but good for making it easy to write
        val charMap = mapOf(redChar to ANSI_RED, greenChar to ANSI_GREEN, yellowChar to ANSI_YELLOW)
        print(s.map { c: Char ->charMap.getOrDefault(c, ANSI_RESET) + c + ANSI_RESET }.joinToString(""))
    }

    fun previousLine() {
        clearLine()
        print("\u001b[F\r")
    }
}