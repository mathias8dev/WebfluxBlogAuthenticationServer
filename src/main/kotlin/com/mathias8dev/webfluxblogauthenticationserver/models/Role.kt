package com.mathias8dev.webfluxblogauthenticationserver.models

import jakarta.persistence.*

@Table(name = "role")
@Entity
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_authority",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    var authorities: MutableSet<Authority> = mutableSetOf(),
    var name: String
) {

    override fun toString(): String {
        return "Role(id=$id, authorities=$authorities, name='$name')"
    }
}
