package com.itsight.flash.util

import android.content.Context

object MyVeridiumPreferencesManager {

    const val IDENTIFIER = "identifier"
    const val NAME = "name"
    const val FINGER = "finger"

    fun saveIdentifier(context: Context, identifier: String) {
        val editor = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(IDENTIFIER, identifier)
        editor.apply()
    }

    fun saveFinger(context: Context, identifier: String) {
        val editor = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(FINGER, identifier)
        editor.apply()
    }

    fun getIdentifier(context: Context): String {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString(IDENTIFIER, "")!!
    }

    fun getFinger(context: Context): String {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString(FINGER, "")!!
    }

    fun saveName(context: Context, name: String) {
        val editor = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(NAME, name)
        editor.apply()
    }

    fun getName(context: Context): String {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString(NAME, "")!!
    }
}