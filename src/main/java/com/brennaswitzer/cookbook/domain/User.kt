package com.brennaswitzer.cookbook.domain

import javax.persistence.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"])]
)
class User : BaseEntity {
    var name: String? = null
    var email: String? = null
    var imageUrl: String? = null

    @Enumerated(EnumType.STRING)
    var provider: AuthProvider? = null

    var providerId: String? = null

    constructor() {}
    constructor(
        name: String?,
        email: String?
    ) {
        this.name = name
        this.email = email
    }
}
