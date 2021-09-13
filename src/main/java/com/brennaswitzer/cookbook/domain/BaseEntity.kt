package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.util.IdUtils
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotNull

@MappedSuperclass
abstract class BaseEntity : Identified {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Column(updatable = false)
    private val _eqkey = IdUtils.next(javaClass)

    @Column(name = "created_at")
    var createdAt: @NotNull Instant? = null

    @Version
    @Column(name = "updated_at")
    var updatedAt: @NotNull Instant? = null

    @PrePersist
    protected fun onPrePersist() {
        val now = Instant.now()
        createdAt = now
    }

    /**
     * I indicate object equality, which in this case means an assignable type
     * and the same [.get_eqkey]. Using the `_eqkey` (which is
     * database-persisted) instead of the object's memory location allows for
     * proper operation across the persistence boundary. Using an assignable
     * type (instead of type equality) allows for proper operation across
     * persistence proxies. It has the side effect of allow subtypes to be
     * considered equal, but the `_eqkey` generator embeds type info which
     * will break such ties in the normal case.
     *
     * @param `object` The object to check for equality with this one
     * @return Whether the passed object is equal to this one
     */
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return if (!BaseEntity::class.java.isAssignableFrom(other.javaClass)) false else get_eqkey() == (other as BaseEntity).get_eqkey()
    }

    override fun hashCode(): Int {
        return get_eqkey().hashCode()
    }

    override fun toString(): String {
        return javaClass.simpleName + "#" + id
    }

    fun get_eqkey(): @NotNull Long? {
        return _eqkey
    }

}
