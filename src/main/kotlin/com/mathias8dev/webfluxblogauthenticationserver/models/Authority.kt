package com.mathias8dev.webfluxblogauthenticationserver.models

import jakarta.persistence.*

@Entity
@Table(name = "authority")
class Authority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,
    var name: String
) {
    override fun toString(): String {
        return "Authority(id=$id, name='$name')"
    }
}
