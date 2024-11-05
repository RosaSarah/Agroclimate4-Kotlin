package edu.curso.teste

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CadastrarSafraActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_safra)

        // Inicializar a referência ao Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("safras")

        val etCropName: EditText = findViewById(R.id.etCultNome)
        val etStartDate: EditText = findViewById(R.id.etDtInicio)
        val etQuantity: EditText = findViewById(R.id.etQuant)
        val etEndDate: EditText = findViewById(R.id.etDtFinal)
        val btnSubmit: Button = findViewById(R.id.btnEnviar)

        // DatePicker para a Data de Início
        etStartDate.setOnClickListener {
            showDatePickerDialog(etStartDate)
        }

        // DatePicker para a Data de Fim
        etEndDate.setOnClickListener {
            showDatePickerDialog(etEndDate)
        }

        // Ação do botão de enviar
        btnSubmit.setOnClickListener {
            val cropName = etCropName.text.toString()
            val startDate = etStartDate.text.toString()
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
            val endDate = etEndDate.text.toString()

            if (cropName.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && quantity > 0) {
                val safra = Safra(cropName, startDate, endDate, quantity)
                salvarNoFirebase(safra)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos corretamente", Toast.LENGTH_LONG).show()
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

    // Função para salvar no Firebase
    private fun salvarNoFirebase(safra: Safra) {
        val safraId = database.push().key ?: return
        database.child(safraId).setValue(safra)
            .addOnSuccessListener {
                Toast.makeText(this, "Safra cadastrada com sucesso!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cadastrar safra!", Toast.LENGTH_LONG).show()
            }
    }

}
