package com.devlogs.chatty.common.helper

fun String.isEmail () : Boolean {

    if (!contains('@') && !contains('.')) {
        return false
    }

    val emailName = substring(0, indexOf('@'))
    val emailDomain = substring(indexOf('@'), indexOf('.'))
    val domainType = substring(indexOf('.'), length)

    val validEmailName = !(emailName.contains('@') || emailName.contains('.') || emailName.isBlank())
    val validEmailDomain = !(emailDomain.contains('@') || emailDomain.contains('.') || emailDomain.isBlank())
    val validDomainType = !domainType.isBlank()


    return validEmailDomain && validDomainType && validEmailName
}

fun String.isValidPassword () : Boolean {

    return length > 6
}

