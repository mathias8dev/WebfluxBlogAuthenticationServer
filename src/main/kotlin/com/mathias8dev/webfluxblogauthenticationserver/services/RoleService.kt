package com.mathias8dev.webfluxblogauthenticationserver.services

import com.mathias8dev.webfluxblogauthenticationserver.models.Role


interface RoleService {
    fun findByName(name: String): Role
    val defaultRole: Role
}