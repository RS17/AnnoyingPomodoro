package com.github.rs17.annoyingpomodoro_desktop

import java.io.BufferedReader
import java.io.InputStreamReader

class CLIIO {
    // special class to deal with annoyances when reading from CLI
    // simply stores every user input in a list that can be retrieved
    // so the application can ask "Did the user just hit enter (or type something and hit enter)?"  And this can answer.
    var bufferedReader: BufferedReader = BufferedReader(InputStreamReader(System.`in`))
    // this will take a resource from CLI and trigger it on setting user input

    private val output = mutableListOf<String>()

    // colors should match common names in lowercase as used in android (for library compatibility)
    val colorMap = mapOf<String, String>(
        "red" to "\u001B[31m",
        "green" to "\u001B[32m",
        "yellow" to "\u001B[93m",
        "white" to "\u001B[37m",
        "reset" to "\u001B[0m"
    )

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

    fun printOverwriteableColor(s: String, ansiColor: String){
        clearLine()
        printOut(ansiColor + s + "\r"+ colorMap["reset"])
    }

    fun printOut(s: String){
        print("   " + s)
    }
    fun printColorLine(s: String, ansiColor: String){
        clearLine()
        printOut(ansiColor + s + colorMap["reset"])
    }
    fun printColorMulti(s: String, redChar: Char, greenChar: Char, yellowChar: Char){
        // not exactly efficient but good for making it easy to write
        val charMap = mapOf(redChar to colorMap["red"], greenChar to colorMap["green"], yellowChar to colorMap["yellow"])
        print(s.map { c: Char ->charMap.getOrDefault(c, colorMap["reset"]) + c + colorMap["reset"]}.joinToString(""))
    }

    fun previousLine() {
        clearLine()
        print("\u001b[F\r")
    }
}