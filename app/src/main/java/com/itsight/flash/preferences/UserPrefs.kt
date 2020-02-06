package com.itsight.flash.preferences

import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import com.itsight.flash.model.User

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
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(SYSTEM_TOKEN, systemToken)
        }
    }

    fun getSystemToken(context: Context?): String {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(SYSTEM_TOKEN, "")!!
        }
    }

    fun isLoggedIn(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            !TextUtils.isEmpty(getString(USER_TOKEN, ""))
        }
    }

    fun putPhone(context: Context?, phone: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(USER_PHONE, phone)
        }
    }

    fun getUserPhone(context: Context?): String {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(USER_PHONE, "")!!
        }
    }

    fun updateUser(context: Context?, user: User) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            /*putLong(USER_ID, user.id)*/
            putString(USER_NAME, user.name)
            putString(USER_LAST_NAME, user.lastName)
            putString(USER_EMAIL, user.email)
            putString(USER_DNI, user.dni)
        }
    }

    fun putUser(context: Context, user: User, userToken: String, expirationTime: Long) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(USER_TOKEN, userToken)
            putLong(USER_TOKEN_EXPIRATION_TIME, expirationTime)
            /*putLong(USER_ID, user.id)*/
            putString(USER_NAME, user.name)
            putString(USER_LAST_NAME, user.lastName)
            putString(USER_EMAIL, user.email)
            putString(USER_DNI, user.dni)
        }
    }

    fun getUser(context: Context?): User {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            val token = getString(USER_TOKEN, "")
            val expires = getLong(USER_TOKEN_EXPIRATION_TIME, 0)
            val id = getLong(USER_ID, 0)
            val name = getString(USER_NAME, "")
            val lastName = getString(USER_LAST_NAME, "")
            val email = getString(USER_EMAIL, "")
            val dni = getString(USER_DNI, "")
            val referralCode = getString(USER_REFERRAL_CODE, "")
            val referralUrl = getString(USER_REFERRAL_URL, "")
            val phone = getString(USER_PHONE, "")

            User("", "", "", "", "", "")
        }
    }

    fun clear(context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).delete {
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
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(USER_BIOMETRIC_WRONG_ATTEMPTS, wrongAttempts)
        }
    }

    fun getUserBiometricWrongAttempts(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getInt(USER_BIOMETRIC_WRONG_ATTEMPTS, 0)
        }
    }

    fun putHideCarousel(context: Context, estado: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putBoolean(USER_HIDE_CAROUSEL, estado)
        }
    }

    fun getHideCarousel(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getBoolean(USER_HIDE_CAROUSEL, false)
        }
    }
}