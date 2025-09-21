package com.br.listadecompras.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.listadecompras.Session
import com.br.listadecompras.data.model.User
import com.br.listadecompras.data.repository.UserDAO

class LoginViewModel : ViewModel() {
    private val userDAO = UserDAO()

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    fun login(email: String, password: String) {
        val user = userDAO.login(email, password)
        user?.let { Session.userLogged = it }
        _loginResult.value = user
    }
}
