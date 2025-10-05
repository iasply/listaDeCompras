package com.br.listadecompras.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.User
import com.br.listadecompras.data.repository.UserDAO

class LoginViewModel : ViewModel() {

    private val userDAO = UserDAO()

    sealed class UiState {
        data class Success(val user: User) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun login(email: String, password: String) {

        if (password.isBlank() || email.isBlank()) {
            _state.value = UiState.Error("Os campos devem ser preenchidos")
            return
        }
        
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = UiState.Error("E-mail inválido")
            return
        }
        val user = userDAO.login(email, password)
        if (user != null) {
            Session.userLogged = user
            _state.value = UiState.Success(user)
        } else {
            _state.value = UiState.Error("Usuário ou senha incorretos")
        }
    }
}
