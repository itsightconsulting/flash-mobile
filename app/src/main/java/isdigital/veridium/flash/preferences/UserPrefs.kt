package isdigital.veridium.flash.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import isdigital.veridium.flash.model.dto.BestFingers
import isdigital.veridium.flash.model.dto.Fingers
import isdigital.veridium.flash.model.pojo.ActivationPOJO

object UserPrefs {

    private const val SYSTEM_TOKEN = "flash.system.prefs.SYSTEM_TOKEN"
    private const val USER_TOKEN = "flash.user.prefs.USER_TOKEN"
    private const val USER_TOKEN_EXPIRATION_TIME = "flash.user.prefs.USER_TOKEN_EXPIRATION_TIME"
    private const val USER_ID = "flash.user.prefs.ID"
    private const val USER_NAME = "flash.user.prefs.USER_FIRST_NAME"
    private const val USER_LAST_NAME = "flash.user.prefs.USER_LAST_NAME"
    private const val USER_EMAIL = "flash.user.prefs.USER_EMAIL"
    private const val USER_DNI = "flash.user.prefs.USER_DNI"
    private const val USER_REFERRAL_CODE = "flash.user.prefs.REFERRAL_CODE"
    private const val USER_REFERRAL_URL = "flash.user.prefs.REFERRAL_URL"
    private const val USER_PHONE = "flash.user.prefs.PHONE"
    private const val USER_BIOMETRIC_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    private const val USER_ICCID_WRONG_ATTEMPTS = "flash.user.prefs.BIOMETRIC_ATTEMPTS"
    private const val USER_HIDE_CAROUSEL = "flash.user.prefs.HIDE_CAROUSEL"
    private const val USER_HAND_SELECTED = "flash.user.prefs.USER_HAND_SELECTED"

    private const val ACTIVATION_FORM_ID = "flash.user.prefs.ACTIVATION_FORM_ID"
    private const val ACTIVATION_DNI = "flash.user.prefs.ACTIVATION_DNI"
    private const val ACTIVATION_NAME = "flash.user.prefs.ACTIVATION_NAME"
    private const val ACTIVATION_LASTNAME = "flash.user.prefs.ACTIVATION_LASTNAME"
    private const val ACTIVATION_BIRTHDATE = "flash.user.prefs.ACTIVATION_BIRTHDATE"
    private const val ACTIVATION_EMAIL = "flash.user.prefs.ACTIVATION_EMAIL"
    private const val ACTIVATION_WANT_TO_PORTABILITY = "flash.user.prefs.ACTIVATION_WANT_TO_PORTABILITY"
    private const val ACTIVATION_SPONSORTEAMID = "flash.user.prefs.ACTIVATION_SPONSORTEAMID"
    private const val ACTIVATION_PHONENUMBER = "flash.user.prefs.ACTIVATION_PHONENUMBER"
    private const val ACTIVATION_ICCID = "flash.user.prefs.ACTIVATION_ICCID"
    private const val ACTIVATION_CURRENTCOMPANY = "flash.user.prefs.ACTIVATION_CURRENTCOMPANY"
    private const val ACTIVATION_PLANTYPE = "flash.user.prefs.ACTIVATION_PLANTYPE"
    private const val API_TOKEN = "API_TOKEN"

    private const val SCANNER_BEST_FINGERPRINT_LEFT = "flash.user.prefs.fingerprint.left"
    private const val SCANNER_BEST_FINGERPRINT_RIGHT = "flash.user.prefs.fingerprint.right"
    private const val SCANNER_DESC_BEST_FINGERPRINT_LEFT = "flash.user.prefs.fingerprint.left.desc"
    private const val SCANNER_DESC_BEST_FINGERPRINT_RIGHT = "flash.user.prefs.fingerprint.right.desc"

    fun setApiToken(context: Context?, apiToken: String) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(API_TOKEN, apiToken)
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

            remove(ACTIVATION_FORM_ID)
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


    fun putUserBiometricWrongAttempts(context: Context?) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(USER_BIOMETRIC_WRONG_ATTEMPTS, getUserBiometricWrongAttempts(context) + 1)
        }
    }

    fun getUserBiometricWrongAttempts(context: Context?): Int {
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
            putString(ACTIVATION_FORM_ID, activation.formId)
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
            val formId = getString(ACTIVATION_FORM_ID, "")
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

            val form = ActivationPOJO(
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

            form.formId = formId

            form
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

    fun putIccid(context: Context?, iccid: String) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(ACTIVATION_ICCID, iccid)
        }
    }

    fun getIccid(context: Context?): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            getString(ACTIVATION_ICCID, "")
        }
    }

    fun putBestFingerPrints(context: Context, bestFingers: BestFingers) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putInt(SCANNER_BEST_FINGERPRINT_LEFT, bestFingers.codigoIzq.replace("0", "").toInt())
            putInt(SCANNER_BEST_FINGERPRINT_RIGHT, bestFingers.codigoDer.replace("0", "").toInt())
            putString(SCANNER_DESC_BEST_FINGERPRINT_LEFT, bestFingers.descripcionIzq)
            putString(SCANNER_DESC_BEST_FINGERPRINT_RIGHT, bestFingers.descripcionDer)
        }
    }

    fun getBestFingerPrints(context: Context?): Fingers {
        return PreferenceManager.getDefaultSharedPreferences(context).get {
            val right = getInt(SCANNER_BEST_FINGERPRINT_LEFT, -1)
            val left = getInt(SCANNER_BEST_FINGERPRINT_RIGHT, -1)
            val descriptionRight = getString(SCANNER_DESC_BEST_FINGERPRINT_LEFT, "")
            val descriptionLeft = getString(SCANNER_DESC_BEST_FINGERPRINT_RIGHT, "")

            Fingers(
                right,
                left,
                descriptionRight!!,
                descriptionLeft!!
            )
        }
    }

    fun putHandSelected(context: Context?, hand: String) {
        PreferenceManager.getDefaultSharedPreferences(context).put {
            putString(USER_HAND_SELECTED, hand)
        }
    }
}