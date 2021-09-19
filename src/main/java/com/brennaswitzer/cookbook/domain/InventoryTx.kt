package com.brennaswitzer.cookbook.domain

import javax.persistence.*

@Entity // @MappedSuperclass can't back a polymorphic @Repository
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
abstract class InventoryTx : BaseEntity {

    @ManyToOne
    lateinit var item: InventoryItem

    /**
     * The quantity after the transaction.
     */
    @Embedded
    lateinit var quantity: Quantity

    constructor() : super()

    constructor(
        item: InventoryItem,
        quantity: Quantity,
    ) : super() {
        this.item = item
        this.quantity = quantity
    }

    /**
     * The quantity before the transaction. [ResetTx] are destructive, so it is
     * require to track for that type. However, it's useful for hypothetical
     * "discard report" which wants to show only [DiscardTx] instance, but still
     * wants to know the total inventory. Without tracking this, the full
     * transaction would have to be processed.
     */
    @Embedded
    @AttributeOverride(
        name = "quantity",
        column = Column(name = "new_quantity")
    )
    @AssociationOverride(
        name = "units",
        joinColumns = [JoinColumn(name = "new_units_id")]
    )
    var newQuantity: Quantity = Quantity.ZERO

    final fun commit() {
        newQuantity = computeNewQuantity(item.quantity)
    }

    abstract fun computeNewQuantity(curr: Quantity): Quantity

    override fun toString(): String {
        return "${this::class.simpleName}(quantity=$quantity, newQuantity=$newQuantity)"
    }

}

/** You got some! */
@Entity
@DiscriminatorValue("1")
class AcquireTx : InventoryTx {

    constructor() : super()

    constructor(
        item: InventoryItem,
        quantity: Quantity,
    ) : super(item, quantity)

    override fun computeNewQuantity(curr: Quantity) =
        curr + quantity
}

/** You used some! */
@Entity
@DiscriminatorValue("2")
class ConsumeTx : InventoryTx {

    constructor() : super()

    constructor(
        item: InventoryItem,
        quantity: Quantity,
    ) : super(item, quantity)

    override fun computeNewQuantity(curr: Quantity) =
        curr - quantity
}

/** You threw some away! */
@Entity
@DiscriminatorValue("3")
class DiscardTx : InventoryTx {

    constructor() : super()

    constructor(
        item: InventoryItem,
        quantity: Quantity,
    ) : super(item, quantity)

    override fun computeNewQuantity(curr: Quantity) =
        curr - quantity
}

/** You lost track and need a reset! */
@Entity
@DiscriminatorValue("4")
class ResetTx : InventoryTx {

    constructor() : super()

    constructor(
        item: InventoryItem,
        quantity: Quantity,
    ) : super(item, quantity)

    override fun computeNewQuantity(curr: Quantity) =
        quantity
}
