package com.github.rs17.annoyingpomodoro_android

import android.app.AlertDialog
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.github.rs17.annoyingpomodoro_android.AndroidSettingsActivity.SettingsFragment.Companion.hasDancer
import com.github.rs17.annoyingpomodoro_android.AndroidSettingsActivity.SettingsFragment.Companion.hasTickingSoundId
import com.github.rs17.annoyingpomodoro_android.AndroidSettingsActivity.SettingsFragment.Companion.pauseOnCallId
import com.github.rs17.annoyingpomodoro_android.AndroidSettingsActivity.SettingsFragment.Companion.shortBreaksUntilLongId
import com.github.rs17.annoyingpomodoro_lib.StringConst
import com.github.rs17.annoyingpomodoro_lib.TimerRunWork
import com.github.rs17.annoyingpomodoro_lib.UIHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.lang.RuntimeException


class AndroidMainActivity : AppCompatActivity(),
    com.github.rs17.annoyingpomodoro_lib.MainUI {

    val NOTIFICATION_REMAINING_CHANNEL_ID = "AP NOTIFICATION REMAINING ID1"  //must change ID to update notifcation parameters due to caching!
    val NOTIFICATION_DONE_CHANNEL_ID = "AP NOTIFICATION DONE ID"
    val NOTIFICATION_REMAINING_NOTIF_ID = 0
    val NOTIFICATION_DONE_NOTIF_ID = 1
    val notificationRemain = NotificationCompat.Builder(this, NOTIFICATION_REMAINING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_annoying)
            .setContentTitle("Annoying Pomodoro - time remaining")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    val notificationDone = NotificationCompat.Builder(this, NOTIFICATION_DONE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_annoying)
            .setContentTitle("Annoying Pomodoro")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    val appState by lazy{
        com.github.rs17.annoyingpomodoro_lib.UniversalState(
            this
        )
    }

    var runningPlayer : AndroidMediaPlayer? = null

    val listener by lazy{ AndroidCallListener(appState)}

    override val doneResource: Int = R.raw.done
    override val tickTockResource: Int = R.raw.ticktock2
    override val longBreakDurationId: String = AndroidSettingsActivity.SettingsFragment.longBreakDurationId
    override val shortBreakDurationId: String = AndroidSettingsActivity.SettingsFragment.shortBreakDurationId
    override val pomodoroDurationId: String = AndroidSettingsActivity.SettingsFragment.pomodoroDurationId

    override fun onCreate(savedInstanceState: Bundle?) {
        //if this appears to be running on resume, you're probably not actually resuming - more likely the program exited and timerrun didn't stop
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        appState.onCreate()
        createStopButton()
        setupPrefs("")
        TimerRunWork(appState, this).prepare()
        createNotificationChannels()
        val mTM =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mTM.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        update()
    }

    // must be declared out here so it doesn't get garbage collected
    val spcListener = OnSharedPreferenceChangeListener { prefs, key ->
        getPrefs(prefs, key)
    }

    private fun getPrefs(prefs: SharedPreferences, updatedKey: String){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true)

        appState.workMillis = mmss2millis(prefs.getString(pomodoroDurationId, "25:00"))
        appState.shortBreakMillis = mmss2millis(prefs.getString(shortBreakDurationId, "05:00"))
        appState.longBreakMillis = mmss2millis(prefs.getString(longBreakDurationId, "25:00"))
        appState.hasTick = prefs.getBoolean(hasTickingSoundId, true)
        appState.shortBreaksUntilLong = prefs.getString(shortBreaksUntilLongId, "4")!!.toInt()
        appState.pauseOnCall = prefs.getBoolean(pauseOnCallId, true)
        appState.hasDancer = prefs.getBoolean(hasDancer, true)
        when(updatedKey){
            shortBreaksUntilLongId -> appState.pomodorosSinceLongBreak = 0
        }
        appState.currentTimerRun?.update(updatedKey)
        update()
    }

    private fun setupPrefs(updatedKey: String){
        //no universal preferences because preferences are mixed up in Android UI
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(spcListener)
        getPrefs(prefs, updatedKey)
    }


    private fun mmss2millis(str: String?): Long{
        try {
            return UIHelper.mmss2millis(str)
        } catch ( ex: java.lang.Exception) {
            AlertDialog.Builder(this@AndroidMainActivity)
                .setTitle("FAIL")
                .setMessage("Invalid setting: '" + str + "'.  Please go to the settings menu and set any times to format mm:ss")
                .show()
            return 0
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", { _, _ -> finish() })
            .setNegativeButton("No", null)
            .show()
    }

    fun createStopButton(){
        btnStop.setOnClickListener { _ ->
            appState.currentTimerRun!!.pause()
            displayTime(appState.timerMillisRemaining)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> showSettings()
            R.id.action_log -> showLog()
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showSettings(): Boolean{
        val settingsIntent = Intent(this, AndroidSettingsActivity::class.java)
        startActivity(settingsIntent)
        return true
    }

    fun showLog(): Boolean {
        val logViewIntent = Intent(this, AndroidLogViewActivity::class.java)
        logViewIntent.putExtra("logFilePath", appState.pomodoroLogger.logFilePath)
        startActivity(logViewIntent)
        return true
    }

    override fun update(){
        lblPomodoroCount.setText(StringConst.POMS_THIS_SESSION.message + appState.pomodorosThisSession)
        lblShortBreaksUntilLong.setText(StringConst.POMS_TILL_LONG_BREAK.message + (appState.shortBreaksUntilLong - appState.pomodorosSinceLongBreak))
    }



    override fun displayTime(time: Long){
        // this runs a lot, keep it simple as possible
        val timeRemain: String = UIHelper.timeFormatRemain(time)
        runOnUiThread {
            TimeRemaining.setText(timeRemain);
            lblDancer.setText(appState.currentTimerRun?.dancerMessage())
            notificationRemain.setContentText(timeRemain)
            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_REMAINING_NOTIF_ID, notificationRemain.build())
            }
        }
    }

    override fun switchToStart(){
        runOnUiThread{
            btnStop.hide()
            btnStart.show()
        }
    }
    override fun switchToStop(){
        runOnUiThread {
            btnStop.show()
            btnStart.hide()
        }
    }

    override fun setToResume(){
        // resume after pause
        switchToStart()
    }

    private fun createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelRemain = NotificationChannel(NOTIFICATION_REMAINING_CHANNEL_ID, "AP Time Remaining Notification", NotificationManager.IMPORTANCE_LOW)
            channelRemain.lockscreenVisibility = VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channelRemain)

            val channelDone = NotificationChannel(NOTIFICATION_DONE_CHANNEL_ID,"AP Done Notification", NotificationManager.IMPORTANCE_HIGH)
            channelDone.lightColor = Color.RED // this doesn't seem to work for whatever reason
            channelDone.enableLights(true)
            channelDone.lockscreenVisibility = VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channelDone)
        }
        notificationDone.setContentIntent(mainPendingIntent)
        notificationRemain.setContentIntent(mainPendingIntent)
    }

    override fun onDestroy() {
        // clear all notifications
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_DONE_NOTIF_ID)
        notificationManager.cancel(NOTIFICATION_REMAINING_NOTIF_ID)
        notificationManager.cancelAll()
        appState.onDestroy()
        super.onDestroy()
    }

    override fun handleFinish(finishMessage: String) {
        notificationDone.setContentText(finishMessage)
        with(NotificationManagerCompat.from(this)) {
            cancel(NOTIFICATION_REMAINING_NOTIF_ID)
            notify(NOTIFICATION_DONE_NOTIF_ID, notificationDone.build())
        }
    }

    override fun setOnStart(f:()->Unit){
        btnStart.setOnClickListener{
            f()
            NotificationManagerCompat.from(this).cancel(NOTIFICATION_DONE_NOTIF_ID)
        }
    }

    override fun getFilesDirectory(): File {
        return filesDir
    }

    fun initRunningPlayer(playerResource: Any): AndroidMediaPlayer?{
        return if(playerResource == -1) null else AndroidMediaPlayer(applicationContext, (playerResource as? Int)?: throw RuntimeException("Player resource  is not an int"))
    }

    override fun startRunningPlayer(playerResource: Any) {
        if( runningPlayer != null) killRunningPlayer()
        runningPlayer = initRunningPlayer(playerResource)
        runningPlayer?.start()
    }

    override fun killRunningPlayer() {
        runningPlayer?.stop()
        runningPlayer = null
    }

    val mainPendingIntent: PendingIntent
        get() = PendingIntent.getActivity(
                this, 0, mainIntent
               , PendingIntent.FLAG_UPDATE_CURRENT
            )
    val mainIntent: Intent by lazy{
        Intent(this.applicationContext, AndroidMainActivity::class.java)
    }
}