package com.github.rs17.annoyingpomodoro_android

import android.os.Bundle
import android.text.InputType

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat


class AndroidSettingsActivity: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_view)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.preferences,
                SettingsFragment()
            )
            .commit()
    }




    class SettingsFragment : PreferenceFragmentCompat() {
        companion object {
            val pomodoroDurationId = "pomodoro_duration_0"
            val shortBreakDurationId = "short_break_duration_0"
            val longBreakDurationId = "long_break_duration_0"
            val hasTickingSoundId = "has_ticking_sound_0"
            val shortBreaksUntilLongId = "short_breaks_until_long_0"
            val pauseOnCallId = "pause_on_call_0"
            val hasDancer = "has_dancer_0"
        }
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            setupPref(pomodoroDurationId, InputType.TYPE_CLASS_DATETIME)
            setupPref(shortBreakDurationId, InputType.TYPE_CLASS_DATETIME)
            setupPref(longBreakDurationId, InputType.TYPE_CLASS_DATETIME)
            setupPref(shortBreaksUntilLongId, InputType.TYPE_CLASS_NUMBER)
        }

        fun setupPref(key: String, inputTypeId: Int){
            val pref: EditTextPreference? = preferenceManager.findPreference<EditTextPreference>(key)
            pref!!.setOnBindEditTextListener { editText ->
                editText.setInputType(inputTypeId)
            }
        }
    }
}
