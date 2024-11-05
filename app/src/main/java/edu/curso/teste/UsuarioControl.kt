package edu.curso.teste

import android.util.Log


class UsuarioControl {

    private val repository = UserRepository()
    private val listaUsuario = ArrayList<Usuario>()

    fun salvar(usuario: Usuario){
        repository.salvarFirebase(usuario)

    }

    fun lerTodos(){
        repository.lerTodosFirebase(
            sucesso = {
                listaUsuario.clear()
                listaUsuario.addAll(it)
                Log.i("USUARIO", "UsuarioControl recebeu lista com sucesso $listaUsuario")

            },
            falha = {
                Log.e("USUARIO", "UsuarioControl erro $it ao executar LerTodos ")

        })
    }
}