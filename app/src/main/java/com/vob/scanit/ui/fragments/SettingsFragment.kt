package com.vob.scanit.ui.fragments

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.vob.scanit.R

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("privacy")?.onPreferenceClickListener = this
        findPreference<Preference>("licence")?.onPreferenceClickListener = this

        val pref = findPreference<SwitchPreferenceCompat>("theme_toggle")
        if (pref != null) {
            pref.onPreferenceChangeListener = this
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {

        if (preference != null) {
            when (preference.key) {
                "privacy" -> {
                    Toast.makeText(context, "Privacy Policy", Toast.LENGTH_SHORT).show()
                    //TODO
                }
                "licence" -> {
                    Toast.makeText(context, "Open source licences", Toast.LENGTH_SHORT).show()
                    //TODO
                }
            }
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference is SwitchPreferenceCompat) {
            if (preference.key == "theme_toggle") {
                if (preference.isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        return true
    }
}