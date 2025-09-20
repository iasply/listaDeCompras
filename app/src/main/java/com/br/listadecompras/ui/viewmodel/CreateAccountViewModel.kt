package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.data.model.User
import com.br.listadecompras.data.repository.UserDAO

class CreateAccountViewModel : ViewModel() {
    private val userDAO: UserDAO = UserDAO()
    private val _createAccountResult = MutableLiveData<String>()
    val createAccountResult: LiveData<String> = _createAccountResult

    fun createAccount(name: String, email: String, password: String, confirmPassword: String) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                _createAccountResult.value = "Todos os campos são obrigatórios"
            password != confirmPassword ->
                _createAccountResult.value = "As senhas não coincidem"
            else -> {
                val success = userDAO.createUser(User(null, name, password, email))
                _createAccountResult.value = if (success) {
                    "Cadastro realizado com sucesso"
                } else {
                    "Usuário já existe"
                }
            }
        }
    }
}