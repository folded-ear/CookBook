package com.brennaswitzer.cookbook.domain

import com.fasterxml.jackson.annotation.JsonTypeName
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("PantryItem")
@JsonTypeName("PantryItem")
class PantryItem : Ingredient {
    var aisle: String? = null

    // todo: make this user specific
    var storeOrder = 999999999

    constructor() {}
    constructor(name: String?) : super(name) {}

    override fun toString(): String {
        return name!!
    }

    companion object {
        @JvmField
        val BY_STORE_ORDER =
            java.util.Comparator { a: PantryItem?, b: PantryItem? ->
                if (a == null) return@Comparator if (b == null) 0 else 1
                if (b == null) return@Comparator -1
                if (a.storeOrder != b.storeOrder) return@Comparator a.storeOrder - b.storeOrder
                a.name!!.compareTo(b.name!!, ignoreCase = true)
            }
    }
}
