package pe.mobile.cuy.preferences

import android.content.Context
import android.text.TextUtils

object UserPrefs {

    val SYSTEM_TOKEN = "flash.system.prefs.SYSTEM_TOKEN"
    val USER_TOKEN = "flash.user.prefs.USER_TOKEN"
    val USER_TOKEN_EXPIRATION_TIME = "flash.user.prefs.USER_TOKEN_EXPIRATION_TIME"
    val USER_ID = "flash.user.prefs.ID"
    val USER_NAME = "flash.user.prefs.USER_FIRST_NAME"
    val USER_LAST_NAME = "flash.user.prefs.USER_LAST_NAME"
    val USER_EMAIL = "flash.user.prefs.USER_EMAIL"
    val USER_DNI = "flash.user.prefs.UER_DNI"
    val USER_REFERRAL_CODE = "flash.user.prefs.REFERRAL_CODE"
    val USER_REFERRAL_URL = "flash.user.prefs.REFERRAL_URL"
    val USER_PHONE = "flash.user.prefs.PHONE"
    val USER_BIOMETRIC_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    val USER_HIDE_CAROUSEL = "flash.user.prefs.HIDE_CAROUSEL"

    fun setSystemToken(context: Context?, systemToken: String) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(SYSTEM_TOKEN, systemToken)
        }
    }

    fun getSystemToken(context: Context?): String {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(SYSTEM_TOKEN, "")!!
        }
    }

    fun isLoggedIn(context: Context?): Boolean {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).get {
            !TextUtils.isEmpty(getString(USER_TOKEN, ""))
        }
    }

    fun putPhone(context: Context?, phone: String?) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(USER_PHONE, phone)
        }
    }

    fun getUserPhone(context: Context?): String {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(USER_PHONE, "")!!
        }
    }

    fun clear(context: Context?) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).delete {
            remove(SYSTEM_TOKEN)
            remove(USER_TOKEN)
            remove(USER_TOKEN_EXPIRATION_TIME)
            remove(USER_ID)
            remove(USER_NAME)
            remove(USER_LAST_NAME)
            remove(USER_EMAIL)
            remove(USER_DNI)
            remove(USER_REFERRAL_CODE)
            remove(USER_REFERRAL_URL)
            remove(USER_PHONE)
            remove(USER_BIOMETRIC_WRONG_ATTEMPTS)
        }
    }


    fun putUserBiometricWrongAttempts(context: Context, wrongAttempts: Int) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(USER_BIOMETRIC_WRONG_ATTEMPTS, wrongAttempts)
        }
    }

    fun getUserBiometricWrongAttempts(context: Context): Int {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).get {
            getInt(USER_BIOMETRIC_WRONG_ATTEMPTS, 0)
        }
    }

    fun putHideCarousel(context: Context, estado: Boolean) {
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).put {
            putBoolean(USER_HIDE_CAROUSEL, estado)
        }
    }

    fun getHideCarousel(context: Context): Boolean {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).get {
            getBoolean(USER_HIDE_CAROUSEL, false)
        }
    }
}