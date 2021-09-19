package com.brennaswitzer.cookbook.repositories

import com.brennaswitzer.cookbook.domain.InventoryItem
import com.brennaswitzer.cookbook.domain.InventoryTx

interface InventoryTxRepository : BaseEntityRepository<InventoryTx> {

    fun findByItemOrderByCreatedAt(item: InventoryItem): Iterable<InventoryTx>

}
