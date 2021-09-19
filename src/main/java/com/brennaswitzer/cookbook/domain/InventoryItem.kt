package com.brennaswitzer.cookbook.domain

import org.hibernate.annotations.Formula
import javax.persistence.*

@Entity
class InventoryItem : BaseEntity {

    constructor() : super()

    constructor(
        user: User,
        pantryItem: PantryItem,
    ) : super() {
        this.user = user
        this.pantryItem = pantryItem
    }

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    lateinit var user: User

    @ManyToOne
    lateinit var pantryItem: PantryItem

    @OneToMany(
        targetEntity = InventoryTx::class,
        orphanRemoval = true,
        mappedBy = "item",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    lateinit var transactions: MutableList<InventoryTx>

    /**
     * This is a cache over [transactions].
     */
    @Embedded
    var quantity: Quantity = Quantity.ZERO

    /**
     * This is a cache over [transactions].
     */
    @Formula("(select count(*) from inventory_tx tx where tx.item_id = id)")
    var txCount: Int = 0

    fun acquire(quantity: Quantity) {
        addTransaction(
            AcquireTx(
                this,
                quantity,
            )
        )
    }

    fun consume(quantity: Quantity) {
        addTransaction(
            ConsumeTx(
                this,
                quantity,
            )
        )
    }

    fun discard(quantity: Quantity) {
        addTransaction(
            DiscardTx(
                this,
                quantity,
            )
        )
    }

    fun reset(quantity: Quantity) {
        addTransaction(
            ResetTx(
                this,
                quantity,
            )
        )
    }

    private fun addTransaction(tx: InventoryTx) {
        if (!this::transactions.isInitialized) {
            this.transactions = mutableListOf(tx)
        } else {
            transactions.add(tx)
        }
        tx.commit()
        quantity = tx.newQuantity
        txCount += 1
    }

    override fun toString(): String {
        return "InventoryItem(pantryItem=$pantryItem, available=$quantity)"
    }

}
