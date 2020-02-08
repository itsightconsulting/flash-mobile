package com.itsight.flash.util

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

val GENERIC_ERROR_MESSAGE = "Estamos teniendo incovenientes. Intentelo nuevamente más tarde"


const val API_BASE = "https://epay-auth.azurewebsites.net"

const val API_VERSION_V1 = "/api/v1"

