package edu.curso.teste

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

class GerenciarSafraActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var safraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_safra)

        // Inicializar a referência ao Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("safras")

        // Inicializar os EditTexts e Buttons corretamente
        val etNome: EditText = findViewById(R.id.etNome)
        val etDtInicio: EditText = findViewById(R.id.etDtInicio)
        val etDtFinal: EditText = findViewById(R.id.etDtFinal)
        val etQuant: EditText = findViewById(R.id.etQuant)
        val btnBuscar: Button = findViewById(R.id.btnBuscar)
        val btnAtualizar: Button = findViewById(R.id.btnAtualizar)
        val btnDeletar: Button = findViewById(R.id.btnDeletar)

        // Configurar DatePicker para as datas
        etDtInicio.setOnClickListener {
            showDatePickerDialog(etDtInicio)
        }
        etDtFinal.setOnClickListener {
            showDatePickerDialog(etDtFinal)
        }

        // Ação do botão de buscar
        btnBuscar.setOnClickListener {
            val nomeSafra = etNome.text.toString()
            if (nomeSafra.isNotEmpty()) {
                buscarDados(nomeSafra, etNome, etDtInicio, etDtFinal, etQuant)
            } else {
                Toast.makeText(this, "Por favor, informe o nome da safra", Toast.LENGTH_LONG).show()
            }
        }

        // Ação do botão de atualizar
        btnAtualizar.setOnClickListener {
            if (safraId != null) {
                val cropName = etNome.text.toString()
                val startDate = etDtInicio.text.toString()
                val quantity = etQuant.text.toString().toIntOrNull() ?: 0
                val endDate = etDtFinal.text.toString()

                if (cropName.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && quantity > 0) {
                    val safra = Safra(cropName, startDate, endDate, quantity)
                    atualizarDados(safra)
                } else {
                    Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Safra não encontrada para atualização", Toast.LENGTH_LONG).show()
            }
        }

        // Ação do botão de deletar
        btnDeletar.setOnClickListener {
            if (safraId != null) {
                deletarDados()
            } else {
                Toast.makeText(this, "Safra não encontrada para exclusão", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            editText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }

    // Função para buscar dados no Firebase
    private fun buscarDados(nomeSafra: String, etNome: EditText, etDtInicio: EditText, etDtFinal: EditText, etQuant: EditText) {
        database.orderByChild("nome").equalTo(nomeSafra).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (safraSnapshot in snapshot.children) {
                        val safra = safraSnapshot.getValue(Safra::class.java)
                        safra?.let {
                            etNome.setText(it.nome)
                            etDtInicio.setText(it.dataInicio)
                            etDtFinal.setText(it.dataFim)
                            etQuant.setText(it.quantidade.toString())
                            Toast.makeText(this@GerenciarSafraActivity, "Safra encontrada", Toast.LENGTH_LONG).show()
                        }
                        safraId = safraSnapshot.key
                        break
                    }
                } else {
                    Toast.makeText(this@GerenciarSafraActivity, "Safra não encontrada", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GerenciarSafraActivity, "Erro ao buscar dados: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Função para atualizar dados no Firebase
    private fun atualizarDados(safra: Safra) {
        safraId?.let {
            database.child(it).setValue(safra)
                .addOnSuccessListener {
                    Toast.makeText(this, "Safra atualizada com sucesso!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar safra!", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Função para deletar dados no Firebase
    private fun deletarDados() {
        safraId?.let {
            database.child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Safra deletada com sucesso!", Toast.LENGTH_LONG).show()
                    safraId = null
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao deletar safra!", Toast.LENGTH_LONG).show()
                }
        }
    }

}
