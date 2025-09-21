package com.br.listadecompras.data.repository

import com.br.listadecompras.data.model.User

class UserDAO {
    val db: Db = Db

    fun createUser(user: User): Boolean {
        val exist = db.users.find { it.email == user.email }
        if (exist == null) {
            user.id = this.db.idUser
            db.idUser++
            db.users.add(user)
            return true
        }
        return false
    }

    fun login(email: String, password: String): User? {
        val user = db.users.find { it.email == email && it.password == password }
        return user
    }

}