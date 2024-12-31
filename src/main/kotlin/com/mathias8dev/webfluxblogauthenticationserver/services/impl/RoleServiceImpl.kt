package com.mathias8dev.webfluxblogauthenticationserver.services.impl

import com.mathias8dev.webfluxblogauthenticationserver.models.Role
import com.mathias8dev.webfluxblogauthenticationserver.repositories.RoleRepository
import com.mathias8dev.webfluxblogauthenticationserver.services.RoleService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.management.relation.RoleNotFoundException


@Service
@Transactional
class RoleServiceImpl(
    private val roleRepository: RoleRepository
) : RoleService {

    override fun findByName(name: String): Role {
        return roleRepository.findByName(name).orElseThrow {
            RoleNotFoundException("Unable to found role: $name")
        }
    }

    override val defaultRole: Role
        get() = findByName(DEFAULT_ROLE)

    companion object {
        const val DEFAULT_ROLE: String = "USER"
    }
}
