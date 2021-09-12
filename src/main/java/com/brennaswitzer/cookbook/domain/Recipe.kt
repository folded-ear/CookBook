package com.brennaswitzer.cookbook.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.*
import javax.persistence.*

@Entity
@DiscriminatorValue("Recipe")
@JsonTypeName("Recipe")
class Recipe : Ingredient, AggregateIngredient, Owned {
    // this will gracefully store the same way as an @Embedded Acl will
    @ManyToOne
    private var owner: User? = null

    // these will gracefully emulate AccessControlled's owner property
    @JsonIgnore // but hide it from the client :)
    override fun getOwner(): User {
        return owner!!
    }

    override fun setOwner(owner: User) {
        this.owner = owner
    }

    // end access control emulation
    var externalUrl: String? = null
    var directions: String? = null
    var yield: Int? = null
    var calories: Int? = null

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "file.objectKey",
            column = Column(name = "photo")
        ),
        AttributeOverride(
            name = "file.contentType",
            column = Column(name = "photo_type")
        ),
        AttributeOverride(
            name = "file.size",
            column = Column(name = "photo_size")
        ),
        AttributeOverride(
            name = "focusTop",
            column = Column(name = "photo_focus_top")
        ),
        AttributeOverride(
            name = "focusLeft",
            column = Column(name = "photo_focus_left")
        )
    )
    private var photo: Photo? = null
    fun getPhoto(): Photo? {
        return photo
    }

    fun getPhoto(create: Boolean): Photo {
        assert(create)
        if (photo == null) {
            photo = Photo()
        }
        return photo!!
    }

    fun setPhoto(photo: Photo?) {
        this.photo = photo
    }

    fun setPhoto(file: S3File?) {
        getPhoto(true).file = file
    }

    fun clearPhoto() {
        if (hasPhoto()) {
            photo!!.clearFile()
        }
    }

    fun hasPhoto(): Boolean {
        return photo != null && photo!!.hasFile()
    }

    /**
     * Time is stored in milliseconds
     */
    var totalTime: Int? = null

    @ElementCollection
    @OrderBy("_idx, raw")
    override var ingredients: MutableList<IngredientRef>? = null

    constructor() {}
    constructor(name: String?) {
        this.name = name
    }

    override fun addIngredient(
        quantity: Quantity?,
        ingredient: Ingredient,
        preparation: String?
    ) {
        ensureIngredients()
        ingredients!!.add(IngredientRef(quantity, ingredient, preparation))
    }

    private fun ensureIngredients() {
        if (ingredients == null) ingredients = LinkedList()
    }

    fun addRawIngredient(raw: String?) {
        ensureIngredients()
        ingredients!!.add(IngredientRef(raw))
    }

    @PrePersist
    override fun onPrePersist() {
        super.onPrePersist()
        ensureRefOrder()
    }

    @PreUpdate
    protected fun onPreUpdate() {
        ensureRefOrder()
    }

    private fun ensureRefOrder() {
        if (ingredients == null) return
        var order = 0
        for (ref in ingredients!!) ref.set_idx(order++)
    }

    @JsonIgnore
    override fun assemblePantryItemRefs(): Collection<IngredientRef> {
        val refs = LinkedList<IngredientRef>()
        if (ingredients == null) return refs
        for (ref in ingredients!!) {
            if (!ref.hasIngredient()) continue
            val ingredient = ref.ingredient
            if (ingredient is PantryItem) {
                refs.add(ref)
            } else if (ingredient is AggregateIngredient) {
                refs.addAll((ingredient as AggregateIngredient).assemblePantryItemRefs())
            } else {
                throw IllegalStateException("Recipe #" + id + " has non-" + PantryItem::class.java.simpleName + ", non-" + AggregateIngredient::class.java.simpleName + " IngredientRef<" + (if (ingredient == null) "null" else ingredient.javaClass.simpleName) + ">?!")
            }
        }
        return refs
    }

    @JsonIgnore
    override fun assembleRawIngredientRefs(): Collection<IngredientRef> {
        val refs = LinkedList<IngredientRef>()
        if (ingredients == null) return refs
        for (ref in ingredients!!) {
            if (ref.hasIngredient()) {
                val ingredient = ref.ingredient
                if (ingredient is AggregateIngredient) {
                    refs.addAll((ingredient as AggregateIngredient).assembleRawIngredientRefs())
                }
            } else {
                refs.add(ref)
            }
        }
        return refs
    }

    override fun toString(): String {
        return name!!
    }
}
