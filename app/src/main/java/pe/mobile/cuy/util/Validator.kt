package pe.mobile.cuy.util

fun validateOnlyNumber(string: String): Boolean{
    return string.matches("-?\\d+(\\.\\d+)?".toRegex())
}