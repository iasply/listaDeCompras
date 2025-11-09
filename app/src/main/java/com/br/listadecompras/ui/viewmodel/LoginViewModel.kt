package com.br.listadecompras.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.Session
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

class LoginViewModel : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    sealed class UiState {
        object Loading : UiState()
        data class Success(val userEmail: String) : UiState()
        data class Error(val message: String) : UiState()
    }


    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = UiState.Error("Os campos devem ser preenchidos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = UiState.Error("E-mail inválido")
            return
        }

        _state.value = UiState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    Session.refresh()
                    _state.value = UiState.Success(user?.email ?: "")
                } else {
                    val friendly = mapAuthExceptionToMessage(task.exception)
                    _state.value = UiState.Error(friendly)
                }
            }
    }

    private fun mapAuthExceptionToMessage(ex: Exception?): String {
        if (ex == null) return "Erro desconhecido. Tente novamente."

        when (ex) {
            is FirebaseAuthInvalidUserException -> {
                val code = ex.errorCode ?: ""
                return when (code) {
                    "ERROR_USER_NOT_FOUND" -> "E-mail não cadastrado"
                    "ERROR_USER_DISABLED" -> "Conta desativada"
                    else -> "Usuário inválido: ${ex.localizedMessage ?: ex.message}"
                }
            }

            is FirebaseAuthInvalidCredentialsException -> {
                val code = (ex as? FirebaseAuthException)?.errorCode ?: ""
                return when (code) {
                    "ERROR_INVALID_EMAIL" -> "Formato de e-mail inválido"
                    "ERROR_WRONG_PASSWORD" -> "Senha incorreta"
                    else -> "Dados de autenticação inválidos"
                }
            }
        }

        if (ex is FirebaseAuthException) {
            when (ex.errorCode) {
                "ERROR_INVALID_EMAIL" -> return "Formato de e-mail inválido"
                "ERROR_USER_NOT_FOUND" -> return "E-mail não cadastrado"
                "ERROR_WRONG_PASSWORD" -> return "Senha incorreta"
                else -> return ex.localizedMessage ?: "Erro de autenticação: ${ex.message}"
            }
        }

        return ex.localizedMessage ?: ex.message ?: "Erro desconhecido. Tente novamente."
    }
}
