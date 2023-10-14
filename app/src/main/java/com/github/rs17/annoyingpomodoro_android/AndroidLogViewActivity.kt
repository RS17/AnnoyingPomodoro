package com.github.rs17.annoyingpomodoro_android

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.github.rs17.annoyingpomodoro_android.databinding.ContentLogViewBinding
import java.io.File


class AndroidLogViewActivity : AppCompatActivity() {

    private lateinit var binding: ContentLogViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentLogViewBinding.inflate(layoutInflater);
        setContentView(binding.root)
        showLog(File(intent.getStringExtra("logFilePath")))
    }

    fun showLog(logFile: File){
        val logList: List<String>  = if (logFile.exists()) logFile.readLines().reversed() else listOf("No records yet!")
        binding.logTextVew.setText(logList.joinToString(separator = "\n"))
        binding.logTextVew.movementMethod = ScrollingMovementMethod()
    }

}
