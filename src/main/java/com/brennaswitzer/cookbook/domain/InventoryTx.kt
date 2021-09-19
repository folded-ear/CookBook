package com.brennaswitzer.cookbook.domain

import javax.persistence.*

@Entity // @MappedSuperclass can't support a polymorphic @Repository
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
     * The quantity after the transaction.
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
    ) : super(item, quantity) {
        this.priorQuantity = item.quantity
    }

    /**
     * The quantity before the transaction. As ` ResetTx` are destructive, it's
     * impossible to know the prior state without consulting other parts of the
     * history. This allows for that answer to be obtained directly.
     */
    @Embedded
    @AttributeOverride(
        name = "quantity",
        column = Column(name = "prior_quantity")
    )
    @AssociationOverride(
        name = "units",
        joinColumns = [JoinColumn(name = "prior_units_id")]
    )
    lateinit var priorQuantity: Quantity

    val correction
        get() = newQuantity - priorQuantity

    override fun computeNewQuantity(curr: Quantity) =
        quantity

    override fun toString(): String {
        return "ResetTx(quantity=$quantity, priorQuantity=$priorQuantity, correction=$correction)"
    }

}
