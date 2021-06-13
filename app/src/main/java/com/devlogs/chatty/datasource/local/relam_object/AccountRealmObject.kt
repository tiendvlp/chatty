package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.domain.entity.AccountEntity
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class AccountRealmObject : RealmObject {
    @PrimaryKey
    var id: String? = null
    @Required
    var email: String? = null
    @Required
    var name: String? = null

    constructor(id: String?, email: String?, name: String?) {
        this.id = id
        this.email = email
        this.name = name
    }

    constructor( )
}


fun toAccountEntity (accountRO: AccountRealmObject) =
    AccountEntity(
        id = accountRO.id!!,
        name = accountRO.name!!,
        email = accountRO.email!!
    )

fun toAccountRO (accountEntity: AccountEntity) =
    AccountRealmObject(
        id = accountEntity.id,
        name = accountEntity.name,
        email = accountEntity.email
    )
