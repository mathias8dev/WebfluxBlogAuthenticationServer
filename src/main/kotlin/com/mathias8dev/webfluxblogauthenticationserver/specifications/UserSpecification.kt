package com.mathias8dev.webfluxblogauthenticationserver.specifications

import com.mathias8dev.webfluxblogauthenticationserver.models.Authority
import com.mathias8dev.webfluxblogauthenticationserver.models.Role
import com.mathias8dev.webfluxblogauthenticationserver.models.User
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification


class UserSpecification {
    companion object {
        fun hasAuthorityIn(authorities: List<String>): Specification<User> {
            return Specification<User> { root, query, criteriaBuilder ->
                // Join roles
                val rolesJoin = root.join<User, Role>("roles", JoinType.INNER)
                // Join authorities within roles
                val authoritiesJoin = rolesJoin.join<Role, Authority>("authorities", JoinType.INNER)

                // Build the predicate
                val predicate = authoritiesJoin.get<String>("name").`in`(authorities)
                criteriaBuilder.and(predicate)
            }
        }
    }
}