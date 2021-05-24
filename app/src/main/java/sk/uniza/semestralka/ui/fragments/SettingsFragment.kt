package sk.uniza.semestralka.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import sk.uniza.semestralka.Constants
import sk.uniza.semestralka.R

/**
 * Class used for displaying setting fragment and setting preferences
 *
 */
class SettingsFragment : PreferenceFragmentCompat(),    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs : SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //obtain instance of PreferenceManager
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        //register listener when user changes preference
        prefs.registerOnSharedPreferenceChangeListener(this)
    }


    /**
     * This method is fired when user changes preference
     *
     * @param sharedPreferences
     * @param key
     */
    @SuppressLint("CommitPrefEdits")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key){
            Constants.NOTIFICATIONS_ENABLED_KEY -> {
                val editor = this.prefs.edit()
                //true / false
                //edit preference and apply changes
                editor.putBoolean(Constants.PREFERENCE_NOTIFICATIONS_ENABLED_KEY,sharedPreferences?.getBoolean(Constants.NOTIFICATIONS_ENABLED_KEY,false)!!)
                editor.apply()
            }
            Constants.NOTIFICATIONS_TIME_KEY -> {
                val editor = this.prefs.edit()
                // returns hours 06,09,11,13,15,17,19,22
                //edit preference and apply changes
                editor.putInt(Constants.PREFERENCE_NOTIFICATIONS_TIME_KEY,sharedPreferences?.getString(Constants.NOTIFICATIONS_TIME_KEY,"15")!!.toInt())
                editor.apply()
            }
        }
    }
}