package com.devlogs.chatty.common.helper

fun String.isEmail () : Boolean {

    if (!contains('@') || !contains('.')) {
        return false
    }

    val emailName = substring(0, indexOf('@'))
    val emailDomain = substring(indexOf('@')+1, indexOf('.'))
    val domainType = substring(indexOf('.')+1, length)
    val validEmailName = !emailName.contains('.') && emailName.isNotBlank()
    val validEmailDomain = !emailDomain.contains('@') && emailDomain.isNotBlank()
    val validDomainType = !domainType.contains("..") && domainType.isNotBlank()

    return validEmailDomain && validDomainType && validEmailName
}

fun String.isValidPassword () : Boolean {

    return length > 6
}

