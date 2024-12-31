package com.mathias8dev.webfluxblogauthenticationserver.models

import jakarta.persistence.*
import org.hibernate.Length
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity(name = "User")
@Table(name = "app_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf(),

    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = true, unique = true)
    var email: String? = null,
    var password: String,
    @Column(nullable = true, length = Length.LOB_DEFAULT)
    var metadata: String? = null,
    var active: Boolean = false,
    @Column(name = "credentials_non_expired")
    var credentialsNonExpired: Boolean = true,
    @Column(name = "account_non_expired")
    var accountNonExpired: Boolean = true,
    @Column(name = "account_non_locked")
    var accountNonLocked: Boolean = true,
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, optional = true)
    var oneTimeToken: Ott? = null,
    @CreationTimestamp
    override var createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    override var updatedAt: LocalDateTime = LocalDateTime.now(),
) : HasTimestamp {

    override fun toString(): String {
        return "User(id=$id, roles=$roles, username='$username', email=$email, password='$password', metadata=$metadata, active=$active, credentialsNonExpired=$credentialsNonExpired, accountNonExpired=$accountNonExpired, accountNonLocked=$accountNonLocked, oneTimeToken=$oneTimeToken, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}