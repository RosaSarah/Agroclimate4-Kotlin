package edu.curso.teste
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
class UserRepository {

    private val http = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "https://teste-9ecd9-default-rtdb.firebaseio.com/"

    fun salvarFirebase(usuario: Usuario) {
        val usuarioJson = gson.toJson(usuario)
        val request = Request.Builder()
            .url("${baseUrl}usuario.json")
            .post(usuarioJson.toRequestBody("application/json".toMediaType()))
            .build()

        val responseCallback = object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("USUARIO", "Usuário cadastrado com sucesso")
                } else {
                    Log.e("USUARIO", "Erro ao cadastrar usuário: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("USUARIO", "Erro ${e.message} ao gravar o usuário", e)
            }
        }

        http.newCall(request).enqueue(responseCallback)
    }

    fun lerTodosFirebase(sucesso : ( ArrayList<Usuario> ) -> Unit,
                            falha : (String) -> Unit) {
        val request = Request.Builder()
            .url("${baseUrl}usuario.json")
            .get()
            .build()

        val responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("USUARIO",
                    "Erro ${e.message} ao ler os usuários do Firebase", e)
                falha(e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                val usuarioJson = response.body?.string()
                val listaUsuarios = ArrayList<Usuario>()

                if (usuarioJson != null && usuarioJson != "null") {
                    val typeHash = object : TypeToken<HashMap<String, Usuario>>() {}.type
                    val usuarios: HashMap<String, Usuario> = gson.fromJson(usuarioJson, typeHash)

                    for (item in usuarios.values) {
                        listaUsuarios.add(item)
                    }

                    Log.i("USUARIO",
                        "Usuários recuperados: $listaUsuarios")
                    sucesso(listaUsuarios)
                }
            }
        }

        http.newCall(request).enqueue(responseCallback)
    }
}
