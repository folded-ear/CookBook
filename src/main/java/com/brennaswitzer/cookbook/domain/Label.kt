package com.brennaswitzer.cookbook.domain

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var name: @NotNull String? = null

    constructor() {}
    constructor(name: String?) {
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val label = other as Label
        return id == label.id &&
                name == label.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }
}
