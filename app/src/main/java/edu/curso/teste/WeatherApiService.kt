import edu.curso.teste.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Endpoint para obter a previsão do tempo atual (current weather)
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") cityName: String,       // Nome da cidade
        @Query("apiKey") apiKey: String,     // Chave da API
        @Query("units") units: String = "°C" // Unidade de medida: Celsius por padrão
    ): Call<WeatherResponse>
}
