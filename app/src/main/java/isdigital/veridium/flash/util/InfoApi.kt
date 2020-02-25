package isdigital.veridium.flash.util

enum class PROFILES {
    DEV_HOME, DEV_WORK, DEV_CLOUD, PRODUCTION
}

fun getAPIBaseURLByProfile(profile: String): String {
    return when (profile) {
        PROFILES.DEV_CLOUD.name -> "$API_BASE"
        PROFILES.DEV_WORK.name -> "$API_BASE"
        PROFILES.DEV_HOME.name -> "$API_BASE"
        PROFILES.PRODUCTION.name -> "$API_BASE"
        else -> "/"
    }
}

val API_PROFILE = PROFILES.DEV_WORK.name

val API_BASE_URL = getAPIBaseURLByProfile(API_PROFILE)

const val GENERIC_ERROR_MESSAGE = "Estamos teniendo incovenientes. Intentelo nuevamente más tarde"


const val API_BASE = "https://services.insolutions.pe"

const val API_VERSION_V1 = "/FlashMobile/api/v1"

const val API_USERNAME = "ISFlashActivation"
const val API_PASSWORD = "m^A7QqXxzz^Dk+fk"


