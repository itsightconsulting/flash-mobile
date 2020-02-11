package pe.mobile.cuy.preferences

import android.content.SharedPreferences

fun SharedPreferences.put(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}

fun SharedPreferences.delete(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}

fun <T> SharedPreferences.get(func: SharedPreferences.() -> T): T {
    return func()
}