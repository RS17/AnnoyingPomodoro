package com.github.rs17.annoyingpomodoro_desktop

import java.io.File
import java.io.InputStream
import java.net.URL
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

// behold how much simpler this is than Android's MediaPlayer
class JavaxMediaPlayer(val resourcePath: String) {

    val clip: Clip  = AudioSystem.getClip()
    fun start(){
        // must reopen every time because stream and can only be accessed once
        clip.open(AudioSystem.getAudioInputStream(getResource(resourcePath)))
        clip.start()
        clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
    fun stop(){
        clip.stop()
        clip.close()
    }

    fun getResource(path : String) : URL? {
        return JavaxMediaPlayer::class.java.getResource("/" + path)
        //return JavaxMediaPlayer::class.java.getResourceAsStream("/" + path)
    }
}
