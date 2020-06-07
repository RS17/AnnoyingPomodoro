package com.github.rs17.annoyingpomodoro

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_log_view.*
import java.io.File


class AndroidLogViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_view)
        showLog(File(intent.getStringExtra("logFilePath")))
    }

    fun showLog(logFile: File){
        val logList: List<String>  = if (logFile.exists()) logFile.readLines().reversed() else listOf("No records yet!")
        LogText.setText(logList.joinToString(separator = "\n"))
        LogText.movementMethod = ScrollingMovementMethod()
    }

}
