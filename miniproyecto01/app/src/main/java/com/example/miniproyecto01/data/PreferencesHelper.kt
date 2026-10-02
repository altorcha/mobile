package com.example.miniproyecto01.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("RegistroPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_MATRICULA = "last_matricula"
    }

    fun saveLastMatricula(matricula: String) {
        prefs.edit().putString(KEY_LAST_MATRICULA, matricula).apply()
    }

    fun getLastMatricula(): String {
        return prefs.getString(KEY_LAST_MATRICULA, "") ?: ""
    }
}