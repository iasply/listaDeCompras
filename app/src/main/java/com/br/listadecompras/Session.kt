package com.br.listadecompras

import com.br.listadecompras.data.model.User

 abstract class Session{
     companion object {
         var userLogged: User? = null
     }
}