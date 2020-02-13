package pe.mobile.cuy.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import android.text.TextUtils
import pe.mobile.cuy.model.pojo.ActivationPOJO

object UserPrefs {

    val SYSTEM_TOKEN = "flash.system.prefs.SYSTEM_TOKEN"
    val USER_TOKEN = "flash.user.prefs.USER_TOKEN"
    val USER_TOKEN_EXPIRATION_TIME = "flash.user.prefs.USER_TOKEN_EXPIRATION_TIME"
    val USER_ID = "flash.user.prefs.ID"
    val USER_NAME = "flash.user.prefs.USER_FIRST_NAME"
    val USER_LAST_NAME = "flash.user.prefs.USER_LAST_NAME"
    val USER_EMAIL = "flash.user.prefs.USER_EMAIL"
    val USER_DNI = "flash.user.prefs.USER_DNI"
    val USER_REFERRAL_CODE = "flash.user.prefs.REFERRAL_CODE"
    val USER_REFERRAL_URL = "flash.user.prefs.REFERRAL_URL"
    val USER_PHONE = "flash.user.prefs.PHONE"
    val USER_BIOMETRIC_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    val USER_HIDE_CAROUSEL = "flash.user.prefs.HIDE_CAROUSEL"

    val ACTIVATION_DNI = "flash.user.prefs.ACTIVATION_DNI"
    val ACTIVATION_NAME = "flash.user.prefs.ACTIVATION_NAME"
    val ACTIVATION_LASTNAME = "flash.user.prefs.ACTIVATION_LASTNAME"
    val ACTIVATION_BIRTHDATE = "flash.user.prefs.ACTIVATION_BIRTHDATE"
    val ACTIVATION_EMAIL = "flash.user.prefs.ACTIVATION_EMAIL"
    val ACTIVATION_WANT_TO_PORTABILITY = "flash.user.prefs.ACTIVATION_WANT_TO_PORTABILITY"
    val ACTIVATION_SPONSORTEAMID = "flash.user.prefs.ACTIVATION_SPONSORTEAMID"
    val ACTIVATION_PHONENUMBER = "flash.user.prefs.ACTIVATION_PHONENUMBER"
    val ACTIVATION_CURRENTCOMPANY = "flash.user.prefs.ACTIVATION_CURRENTCOMPANY"
    val ACTIVATION_PLANTYPE = "flash.user.prefs.ACTIVATION_PLANTYPE"


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