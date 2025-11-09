package com.br.listadecompras

import com.br.listadecompras.data.model.User
import com.google.firebase.auth.FirebaseAuth

object Session {

    var userLogged: User? = null
        private set

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            userLogged = User(
                id = it.uid,
                name = it.displayName,
                email = it.email
            )
        }
    }

    fun refresh() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        userLogged = currentUser?.let {
            User(
                id = it.uid,
                name = it.displayName,
                email = it.email
            )
        }
    }

    fun clear() {
        userLogged = null
        FirebaseAuth.getInstance().signOut()
    }
}
