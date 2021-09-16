package com.brennaswitzer.cookbook.domain

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Embeddable
class Acl {

    @ManyToOne
    var owner: @NotNull User? = null
        set(owner) {
            field = owner
            // clear any explicit grant the new owner previously had
            if (grants == null) return
            grants!!.remove(owner)
        }

    @ElementCollection
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "level_id")
    var grants: MutableMap<User?, AccessLevel>? = null

    val grantedUsers: Set<User?>
        get() = if (grants == null) {
            Collections.emptySet()
        } else grants!!.keys

    fun getGrant(user: User?): AccessLevel? {
        requireNotNull(user) { "The null user can't have an access grant." }
        if (user.equals(owner)) return AccessLevel.ADMINISTER
        return if (grants == null) null else grants!![user]
    }

    fun setGrant(user: User?, level: AccessLevel): AccessLevel? {
        requireNotNull(user) { "You can't grant access to the null user." }
        if (user.equals(owner)) throw UnsupportedOperationException()
        if (grants == null) grants = HashMap()
        return grants!!.put(user, level)
    }

    fun deleteGrant(user: User?): AccessLevel? {
        requireNotNull(user) { "You can't revoke access from the null user." }
        if (user.equals(owner)) throw UnsupportedOperationException()
        return if (grants == null) null else grants!!.remove(user)
    }

    fun isPermitted(user: User, level: AccessLevel?): Boolean {
        if (user.equals(owner)) return true
        val grant = getGrant(user) ?: return false
        return grant.includes(level)
    }
}
