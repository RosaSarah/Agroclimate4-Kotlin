package edu.curso.teste

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsyncStorageHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    suspend fun saveUserSession(email: String, senha: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString("user_email", email).putString("user_password", senha).apply()
        }
    }

    suspend fun getUserSession(): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            val email = sharedPreferences.getString("user_email", null)
            val senha = sharedPreferences.getString("user_password", null)
            Pair(email, senha)
        }
    }

    suspend fun clearUserSession() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().clear().apply()
        }
    }
}
