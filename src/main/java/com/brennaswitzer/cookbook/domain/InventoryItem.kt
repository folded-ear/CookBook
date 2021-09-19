package com.brennaswitzer.cookbook.domain

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class InventoryItem : BaseEntity() {

    @ManyToOne
    lateinit var user: User

    @ManyToOne
    lateinit var pantryItem: PantryItem

    var storeOrder: Int? = null

    /**
     * This is a cache over the [InventoryTx] for this [PantryItem].
     */
    @Embedded
    lateinit var available: Quantity

}
