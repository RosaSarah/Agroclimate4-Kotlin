package edu.curso.teste

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CadastroActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializar referência ao Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("usuarios")

        // Captura dos campos de entrada
        val etNome = findViewById<EditText>(R.id.etNome)
        val etCNPJ = findViewById<EditText>(R.id.etCNPJ)
        val etCEP = findViewById<EditText>(R.id.etCEP)
        val etTelefone = findViewById<EditText>(R.id.etTelefone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        // Ação ao clicar no botão "Enviar"
        btnEnviar.setOnClickListener {
            val nome = etNome.text.toString()
            val cnpj = etCNPJ.text.toString()
            val cep = etCEP.text.toString()
            val telefone = etTelefone.text.toString()
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (validarEntradas(nome, cnpj, cep, telefone, email, senha)) {
                val usuario = Usuario(nome, cnpj, cep, telefone, email, senha)
                salvarNoFirebase(usuario)
            } else {
                Toast.makeText(this, "Preencha os campos corretamente!", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Função para validar as entradas
    private fun validarEntradas(
        nome: String,
        cnpj: String,
        cep: String,
        telefone: String,
        email: String,
        senha: String
    ): Boolean {
        if (nome.isEmpty() || cnpj.isEmpty() || cep.isEmpty() || telefone.isEmpty() || senha.isEmpty() || email.isEmpty()) {
            return false
        }
        if (cnpj.length != 14 || cep.length != 8 || telefone.length < 10) {
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        return true
    }

    // Função para salvar o usuário no Firebase
    private fun salvarNoFirebase(usuario: Usuario) {
        // Cria uma referência única para cada usuário
        val usuarioId = database.push().key ?: return
        database.child(usuarioId).setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cadastrar usuário!", Toast.LENGTH_LONG).show()
            }
    }
}

