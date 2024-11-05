package edu.curso.teste

import WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {

    // URL base da OpenWeather API
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // Cria um interceptor para log (opcional)
    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // Cliente HTTP com log
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Instância do Retrofit
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instância do serviço da API
    val api: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
