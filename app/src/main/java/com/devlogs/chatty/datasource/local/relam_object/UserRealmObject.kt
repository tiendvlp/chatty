package com.devlogs.chatty.datasource.local.relam_object

import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.domain.datasource.mainserver.model.UserMainServerModel
import com.devlogs.chatty.domain.entity.user.UserEntity
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class UserRealmObject : RealmObject {
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

fun Mapper.toUserEntity (userRO: UserRealmObject) =
    UserEntity(
        id = userRO.id!!,
        name = userRO.name!!,
        email = userRO.email!!
    )

fun Mapper.toUserRO (userEntity: UserEntity) =
     UserRealmObject(
        id = userEntity.id,
        name = userEntity.name,
        email = userEntity.email
    )

fun Mapper.toUserRO (userMainServerModel: UserMainServerModel) =
    UserRealmObject(
        id = userMainServerModel.id,
        email = userMainServerModel.email,
        name = userMainServerModel.name
    )