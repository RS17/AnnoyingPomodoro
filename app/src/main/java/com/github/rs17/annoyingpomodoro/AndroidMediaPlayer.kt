package com.github.rs17.annoyingpomodoro
import android.content.Context
import android.media.MediaPlayer


// Intentionally stateful MediaPlayer wrapper class because MediaPlayer has state but no info about current state.
class AndroidMediaPlayer(context: Context, resource: Int): MediaPlayer(){

    var currentState = PlayerState.prepared
    var baseMediaPlayer: MediaPlayer = create(context, resource)

    init{
        baseMediaPlayer.setLooping(true)
    }

    enum class PlayerState{
        playing,
        stopped,
        prepared
        //Stop! This is just a MediaPlayer wrapper.  PlayerState should not exceed states of MediaPlayer class.
    }

    // overrides of media player functions to enable state checking.  All functions either stay in the state or make it happen.
    override fun start(){
        when(currentState) {
            PlayerState.stopped -> {
                prepare();
                baseMediaPlayer.isLooping = true
                start()
            }
            PlayerState.prepared -> {currentState=PlayerState.playing; baseMediaPlayer.start()}
            else -> {}
        }
    }

    override fun stop(){
        when(currentState){
            PlayerState.playing -> {
                currentState=PlayerState.stopped
                baseMediaPlayer.stop()
                baseMediaPlayer.isLooping = false      //ZenPhone bug - keeps playing otherwise
            }
            else -> {}
        }
    }

    override fun prepare() {
        when(currentState){
            PlayerState.playing -> {stop();prepare()}
            PlayerState.stopped -> {currentState=PlayerState.prepared; baseMediaPlayer.prepare()}
            else -> {}
        }
    }

}
