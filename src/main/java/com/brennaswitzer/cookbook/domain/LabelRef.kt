package com.brennaswitzer.cookbook.domain

import java.util.*
import javax.persistence.Embeddable
import javax.persistence.ManyToOne

@Embeddable
class LabelRef {
    @ManyToOne
    var label: Label? = null

    constructor() {}
    constructor(name: Label?) {
        label = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val labelRef = other as LabelRef
        return label!!.equals(labelRef.label)
    }

    override fun hashCode(): Int {
        return Objects.hash(label)
    }
}
