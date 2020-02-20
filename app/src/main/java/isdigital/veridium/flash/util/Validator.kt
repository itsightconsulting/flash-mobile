package isdigital.veridium.flash.util

fun validateOnlyNumber(string: String): Boolean{
    return string.matches("-?\\d+(\\.\\d+)?".toRegex())
}