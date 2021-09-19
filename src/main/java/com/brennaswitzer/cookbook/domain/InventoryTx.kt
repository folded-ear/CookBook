package com.brennaswitzer.cookbook.domain

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class InventoryTx : BaseEntity() {

    @ManyToOne
    lateinit var item: InventoryItem

    lateinit var action: InventoryAction

    @Embedded
    lateinit var quantity: Quantity

}
