package com.brennaswitzer.cookbook.repositories

import com.brennaswitzer.cookbook.domain.PantryItem
import org.springframework.data.repository.CrudRepository

interface PantryItemRepository : CrudRepository<PantryItem, Long> {
    fun findByNameIgnoreCaseOrderById(name: String): List<PantryItem>
    fun findAllByNameIgnoreCaseContainingOrderById(name: String): List<PantryItem>
}
