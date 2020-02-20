package isdigital.veridium.flash.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import android.text.TextUtils
import isdigital.veridium.flash.model.pojo.ActivationPOJO

object UserPrefs {

    private val SYSTEM_TOKEN = "flash.system.prefs.SYSTEM_TOKEN"
    private val USER_TOKEN = "flash.user.prefs.USER_TOKEN"
    private val USER_TOKEN_EXPIRATION_TIME = "flash.user.prefs.USER_TOKEN_EXPIRATION_TIME"
    private val USER_ID = "flash.user.prefs.ID"
    private val USER_NAME = "flash.user.prefs.USER_FIRST_NAME"
    private val USER_LAST_NAME = "flash.user.prefs.USER_LAST_NAME"
    private val USER_EMAIL = "flash.user.prefs.USER_EMAIL"
    private val USER_DNI = "flash.user.prefs.USER_DNI"
    private val USER_REFERRAL_CODE = "flash.user.prefs.REFERRAL_CODE"
    private val USER_REFERRAL_URL = "flash.user.prefs.REFERRAL_URL"
    private val USER_PHONE = "flash.user.prefs.PHONE"
    private val USER_BIOMETRIC_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    private val USER_ICCID_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    private val USER_HIDE_CAROUSEL = "flash.user.prefs.HIDE_CAROUSEL"

    private val ACTIVATION_DNI = "flash.user.prefs.ACTIVATION_DNI"
    private val ACTIVATION_NAME = "flash.user.prefs.ACTIVATION_NAME"
    private val ACTIVATION_LASTNAME = "flash.user.prefs.ACTIVATION_LASTNAME"
    private val ACTIVATION_BIRTHDATE = "flash.user.prefs.ACTIVATION_BIRTHDATE"
    private val ACTIVATION_EMAIL = "flash.user.prefs.ACTIVATION_EMAIL"
    private val ACTIVATION_WANT_TO_PORTABILITY = "flash.user.prefs.ACTIVATION_WANT_TO_PORTABILITY"
    private val ACTIVATION_SPONSORTEAMID = "flash.user.prefs.ACTIVATION_SPONSORTEAMID"
    private val ACTIVATION_PHONENUMBER = "flash.user.prefs.ACTIVATION_PHONENUMBER"
    private val ACTIVATION_ICCID = "flash.user.prefs.ACTIVATION_ICCID"
    private val ACTIVATION_CURRENTCOMPANY = "flash.user.prefs.ACTIVATION_CURRENTCOMPANY"
    private val ACTIVATION_PLANTYPE = "flash.user.prefs.ACTIVATION_PLANTYPE"
    private val API_TOKEN = "API_TOKEN"

    fun setApiToken(context: Context?, apiToken: String) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(API_TOKEN, apiToken)
        }
    }

    fun getApiToken(context: Context?): String {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(API_TOKEN, "")!!
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

            //remove(USER_HIDE_CAROUSEL)

            remove(ACTIVATION_DNI)
            remove(ACTIVATION_NAME)
            remove(ACTIVATION_LASTNAME)
            remove(ACTIVATION_BIRTHDATE)
            remove(ACTIVATION_EMAIL)
            remove(ACTIVATION_WANT_TO_PORTABILITY)
            remove(ACTIVATION_SPONSORTEAMID)
            remove(ACTIVATION_PHONENUMBER)
            remove(ACTIVATION_CURRENTCOMPANY)
            remove(ACTIVATION_PLANTYPE)
            remove(ACTIVATION_ICCID)
        }
    }

    fun resetUserBarscanAttempts(context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(USER_ICCID_WRONG_ATTEMPTS, 0)
        }
    }

    fun putUserBarscanAttempts(context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(USER_ICCID_WRONG_ATTEMPTS, getUserBarscanAttempts(context) + 1)
        }
    }

    fun getUserBarscanAttempts(context: Context?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getInt(USER_ICCID_WRONG_ATTEMPTS, 0)
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

    fun putActivation(context: Context, activation: ActivationPOJO) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(ACTIVATION_DNI, activation.dni)

            putString(ACTIVATION_NAME, activation.name)
            putString(ACTIVATION_LASTNAME, activation.lastName)
            putString(ACTIVATION_BIRTHDATE, activation.birthDate)
            putString(ACTIVATION_EMAIL, activation.email)
            putBoolean(ACTIVATION_WANT_TO_PORTABILITY, activation.wantPortability)
            putString(ACTIVATION_SPONSORTEAMID, activation.sponsorTeamId)

            putString(ACTIVATION_PHONENUMBER, activation.phoneNumber)
            putString(ACTIVATION_CURRENTCOMPANY, activation.currentCompany)
            putString(ACTIVATION_PLANTYPE, activation.planType)
        }
    }

    fun getActivation(context: Context?): ActivationPOJO {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            val dni = getString(ACTIVATION_DNI, "")

            val name = getString(ACTIVATION_NAME, "")
            val lastName = getString(ACTIVATION_LASTNAME, "")
            val birthDate = getString(ACTIVATION_BIRTHDATE, "")
            val email = getString(ACTIVATION_EMAIL, "")
            val wantToPortability = getBoolean(ACTIVATION_WANT_TO_PORTABILITY, false)
            val sponsorTeamId = getString(ACTIVATION_SPONSORTEAMID, "")

            val phoneNumber = getString(ACTIVATION_PHONENUMBER, "")
            val currentCompany = getString(ACTIVATION_CURRENTCOMPANY, "")
            val planType = getString(ACTIVATION_PLANTYPE, "")

            ActivationPOJO(
                dni!!,
                name!!,
                lastName!!,
                birthDate!!,
                email!!,
                wantToPortability,
                sponsorTeamId,
                phoneNumber,
                currentCompany,
                planType
            )
        }
    }

    fun putUserDni(context: Context, dni: String) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(USER_DNI, dni)
        }
    }

    fun getUserDni(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(USER_DNI, "")
        }
    }
}