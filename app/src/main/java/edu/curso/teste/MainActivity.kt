package edu.curso.teste

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var asyncStorageHelper: AsyncStorageHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Inicialize o AsyncStorageHelper
        asyncStorageHelper = AsyncStorageHelper(this)

        // Checar se o usuário já está logado
        CoroutineScope(Dispatchers.Main).launch {
            val (email, senha) = asyncStorageHelper.getUserSession()
            if (email != null && senha != null) {
                // Usuário já logado, vá para HomeActivity
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }
        }


        // Inicialize a referência ao Realtime Database
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().getReference("usuarios")

        // Captura dos elementos da interface
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        errorTextView = findViewById(R.id.errorTextView)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountTextView = findViewById<TextView>(R.id.createAccountTextView)

        // Ação para o botão de login
        loginButton.setOnClickListener { realizarLogin() }

        // Ação para o texto de criação de conta
        createAccountTextView.setOnClickListener {
            Toast.makeText(this, "Redirecionando para criação de conta...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun realizarLogin() {
        val emailInput = emailEditText.text.toString()
        val passwordInput = passwordEditText.text.toString()

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            mostrarErro("Preencha todos os campos.")
            return
        }

        // Verifica as credenciais no Firebase
        verificarCredenciaisFirebase(emailInput, passwordInput)
    }

    private fun verificarCredenciaisFirebase(email: String, senha: String) {
        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Usuário encontrado, verificar senha
                        for (userSnapshot in snapshot.children) {
                            val senhaFirebase = userSnapshot.child("senha").getValue(String::class.java)
                            if (senhaFirebase == senha) {
                                // Credenciais corretas, salvar sessão do usuário
                                CoroutineScope(Dispatchers.IO).launch {
                                    asyncStorageHelper.saveUserSession(email, senha) // Salva o usuário no armazenamento
                                    withContext(Dispatchers.Main) {
                                        // Navegue para a HomeActivity
                                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                        finish()
                                    }
                                }
                            } else {
                                mostrarErro("Usuário ou senha incorretos.")
                            }
                        }
                    } else {
                        mostrarErro("Usuário não encontrado.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    mostrarErro("Erro ao acessar o banco de dados: ${error.message}")
                    Log.e("LOGIN", "Erro ao acessar o Firebase", error.toException())
                }
            })
    }

    private fun mostrarErro(mensagem: String) {
        errorTextView.text = mensagem
        errorTextView.visibility = View.VISIBLE
    }
}
