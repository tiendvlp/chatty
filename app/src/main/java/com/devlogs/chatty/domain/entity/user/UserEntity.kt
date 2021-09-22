package com.devlogs.chatty.domain.entity.user

data class UserEntity (
      val id: String,
      val name: String,
      val email: String,
     ) {

    override fun toString(): String {

        return "id: $id \n" +
                "name: $name \n" +
                "email: $email"
    }
}

