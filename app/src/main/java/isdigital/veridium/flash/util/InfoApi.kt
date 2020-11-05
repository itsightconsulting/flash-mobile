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
        else -> "qw34/"
    }
}

val API_PROFILE = PROFILES.DEV_WORK.name

val API_BASE_URL = getAPIBaseURLByProfile(API_PROFILE)

const val GENERIC_ERROR_MESSAGE = "Estamos teniendo incovenientes. Inténtelo nuevamente más tarde"
const val TOKEN_ERROR_MESSAGE = "Hemos teniendo un incoveniente. Vuelve a intentarlo ahora"

//const val API_BASE = "https://services.insolutions.pe" // DEV
//const val API_BASE = "http://200.121.128.89"
const val API_BASE = "http://apiflash.insolutions.pe" // PROD

//const val API_VERSION_V1 = "/FlashMobile/api/v1" // DEV
const val API_VERSION_V1 = "/api/v1"


const val API_USERNAME = "ISFlashActivation"
const val API_PASSWORD = "m^A7QqXxzz^Dk+fk"
