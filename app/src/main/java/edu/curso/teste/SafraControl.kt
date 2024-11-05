import android.util.Log
import edu.curso.teste.Safra
import edu.curso.teste.SafraRepository

class SafraControl {

    private val repository = SafraRepository()
    private val listaSafra = ArrayList<Safra>()

    fun salve (safra: Safra) {
        repository.salveFirebase(safra)
    }

    fun lerFirebase() {
        repository.lerFirebase(
            sucesso = {
                listaSafra.clear()
                listaSafra.addAll(it)
                Log.i("SAFRA", "SafraControl recebeu lista com sucesso $listaSafra")
            },
            falha = {
                Log.e("SAFRA", "SafraControl erro $it ao executar lerTodos")
            }
        )
    }
}
