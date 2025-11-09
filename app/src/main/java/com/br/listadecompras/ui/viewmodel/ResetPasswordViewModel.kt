package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val email: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    fun reset(email: String) {
        if (email.isBlank()) {
            _state.value = UiState.Error("Digite um e-mail válido")
            return
        }

        _state.value = UiState.Loading

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = UiState.Success(email)
                } else {
                    val message = task.exception?.message ?: "Erro ao enviar e-mail"
                    _state.value = UiState.Error(message)
                }
            }
    }
}
