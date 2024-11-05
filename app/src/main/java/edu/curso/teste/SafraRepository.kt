package edu.curso.teste

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class SafraRepository {

    private val http = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "https://teste-9ecd9-default-rtdb.firebaseio.com/"

    fun salveFirebase(safra: Safra) {
        val safraJson = gson.toJson(safra)
        val request = Request.Builder()
            .url("${baseUrl}safra.json")
            .post(safraJson.toRequestBody("application/json".toMediaType()))
            .build()

        val responseCallback = object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("SAFRA", "Safra cadastrado com sucesso")
                } else {
                    Log.e("SAFRA", "Erro ao cadastrar safra: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("SAFRA", "Erro ${e.message} ao gravar o safra", e)
            }
        }

        http.newCall(request).enqueue(responseCallback)
    }

    fun lerFirebase(sucesso : ( ArrayList<Safra> ) -> Unit,
                         falha : (String) -> Unit) {
        val request = Request.Builder()
            .url("${baseUrl}safra.json")
            .get()
            .build()

        val responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("safra",
                    "Erro ${e.message} ao ler os usuários do Firebase", e)
                falha(e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                val safraJson = response.body?.string()
                val listasafras = ArrayList<Safra>()

                if (safraJson != null && safraJson != "null") {
                    val typeHash = object : TypeToken<HashMap<String, Safra>>() {}.type
                    val safras: HashMap<String, Safra> = gson.fromJson(safraJson, typeHash)

                    for (item in safras.values) {
                        listasafras.add(item)
                    }

                    Log.i("safra",
                        "Safras recuperadas: $listasafras")
                    sucesso(listasafras)
                }
            }
        }

        http.newCall(request).enqueue(responseCallback)
    }
}

