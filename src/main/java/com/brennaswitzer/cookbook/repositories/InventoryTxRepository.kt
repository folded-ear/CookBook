package com.brennaswitzer.cookbook.repositories

import com.brennaswitzer.cookbook.domain.InventoryItem
import com.brennaswitzer.cookbook.domain.InventoryTx
import org.springframework.data.domain.Sort

interface InventoryTxRepository : BaseEntityRepository<InventoryTx> {

    fun findByItem(item: InventoryItem): Iterable<InventoryTx>

    fun findByItem(item: InventoryItem, sort: Sort): Iterable<InventoryTx>

}
