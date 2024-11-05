package edu.curso.teste

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val weatherTextView = findViewById<TextView>(R.id.weatherTextView)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val searchButton = findViewById<Button>(R.id.searchButton)

        val apiKey = "cbfca3cdf260d96d8b77e1e40a1c895f"

        // Inicializa a referência do Firebase Database
        database = FirebaseDatabase.getInstance().reference

        searchButton.setOnClickListener {
            val cityName = cityEditText.text.toString()

            if (cityName.isNotBlank()) {
                val call = RetrofitInstance.api.getCurrentWeather(cityName, apiKey)

                call.enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val weatherResponse = response.body()
                            val temperature = weatherResponse?.main?.temp
                            val cityNameResponse = weatherResponse?.name

                            // Exibe os dados na interface do usuário
                            weatherTextView.text = "Cidade: $cityNameResponse, Temp: $temperature°C"

                            // Salva os dados no Firebase
                            saveWeatherDataToFirebase(cityNameResponse, temperature)
                        } else {
                            weatherTextView.text = "Erro ao obter clima!"
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        weatherTextView.text = "Falha na conexão!"
                    }
                })
            } else {
                weatherTextView.text = "Por favor, digite o nome de uma cidade."
            }
        }
    }

    private fun saveWeatherDataToFirebase(city: String?, temperature: Float?) {
        // Verifica se os dados são válidos
        if (city != null && temperature != null) {
            // Cria um objeto para armazenar no Firebase
            val weatherData = mapOf(
                "city" to city,
                "temperature" to temperature
            )

            // Salva os dados em uma referência no Firebase
            database.child("weather").child(city).setValue(weatherData)
                .addOnSuccessListener {
                    println("Dados salvos com sucesso no Firebase!")
                }
                .addOnFailureListener {
                    println("Erro ao salvar dados no Firebase: ${it.message}")
                }
        }
    }
}
