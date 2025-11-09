package com.br.listadecompras.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest

class CreateAccountViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val email: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun createAccount(name: String, email: String, password: String, confirmPassword: String) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _state.value = UiState.Error("Todos os campos são obrigatórios")
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _state.value = UiState.Error("Formato de e-mail inválido")
                return
            }

            password != confirmPassword -> {
                _state.value = UiState.Error("As senhas não coincidem")
                return
            }

            password.length < 6 -> {
                _state.value = UiState.Error("Senha deve ter pelo menos 6 caracteres")
                return
            }
        }

        _state.value = UiState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        it.updateProfile(profileUpdates)
                    }

                    _state.value = UiState.Success(email)
                } else {
                    val message = mapCreateAccountException(task.exception)
                    _state.value = UiState.Error(message)
                }
            }
    }

    private fun mapCreateAccountException(ex: Exception?): String {
        if (ex == null) return "Erro desconhecido ao criar conta."

        return when (ex) {
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso"
            is FirebaseAuthWeakPasswordException -> {
                ex.reason ?: "Senha fraca. Use pelo menos 6 caracteres"
            }

            is FirebaseAuthException -> {
                when (ex.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Formato de e-mail inválido"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "E-mail já cadastrado"
                    else -> ex.localizedMessage ?: "Erro de autenticação: ${ex.message}"
                }
            }

            else -> ex.localizedMessage ?: ex.message ?: "Erro desconhecido"
        }
    }
}
