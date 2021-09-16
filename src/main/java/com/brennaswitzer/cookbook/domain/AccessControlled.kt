package com.brennaswitzer.cookbook.domain

import org.springframework.security.access.AccessDeniedException

interface AccessControlled : Owned {
    val acl: Acl?
    override var owner: User?
        get() = acl!!.owner
        set(owner) {
            acl!!.owner = owner
        }

    fun isPermitted(user: User?, level: AccessLevel?): Boolean {
        val acl = acl ?: return false
        return acl.isPermitted(user!!, level)
    }

    fun ensurePermitted(user: User?, level: AccessLevel?) {
        if (!isPermitted(user, level)) {
            throw AccessDeniedException("Unauthorized")
        }
    }
}
