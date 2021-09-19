package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.repositories.InventoryItemRepository
import com.brennaswitzer.cookbook.repositories.InventoryTxRepository
import com.brennaswitzer.cookbook.repositories.PantryItemRepository
import com.brennaswitzer.cookbook.util.RecipeBox
import com.brennaswitzer.cookbook.util.UserPrincipalAccess
import com.brennaswitzer.cookbook.util.WithAliceBobEve
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
@WithAliceBobEve
class InventoryTest {

    @Autowired
    lateinit var itemRepo: InventoryItemRepository

    @Autowired
    lateinit var txRepo: InventoryTxRepository

    @Autowired
    lateinit var pantryItemRepo: PantryItemRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var principalAccess: UserPrincipalAccess

    @Test
    fun doesItSmoke() {
        val box = RecipeBox()
        box.persist(entityManager, principalAccess.user)

        val salt = InventoryItem()
        salt.user = principalAccess.user
        salt.pantryItem = box.salt
        salt.available = Quantity(2.75, box.cup)
        salt.storeOrder = 123
        itemRepo.save(salt)

        assertEquals(1, itemRepo.count())

        var tx = InventoryTx()
        tx.item = salt
        tx.action = InventoryAction.ACQUIRE
        tx.quantity = Quantity(123456, box.cup)
        txRepo.save(tx)

        // bought some more in here, but didn't write it down
        (4..19).forEach {
            tx = InventoryTx()
            tx.item = salt
            tx.action = InventoryAction.CONSUME
            tx.quantity = Quantity(it, box.tbsp)
            txRepo.save(tx)
        }

        // I just checked; I have 3 cups of salt.
        tx = InventoryTx()
        tx.item = salt
        tx.action = InventoryAction.RESET
        tx.quantity = Quantity(3, box.cup)
        txRepo.save(tx)

        // used 4 Tbsp (half cup)
        tx = InventoryTx()
        tx.item = salt
        tx.action = InventoryAction.CONSUME
        tx.quantity = Quantity(4, box.tbsp)
        txRepo.save(tx)
        assertEquals(19, txRepo.count())

        assertEquals(
            salt.available,
            txRepo.findByItemOrderByCreatedAt(salt)
                .fold(Quantity.ZERO) { agg, it ->
                    when (it.action) {
                        InventoryAction.ACQUIRE -> agg + it.quantity
                        InventoryAction.CONSUME, InventoryAction.DISCARD -> agg - it.quantity
                        InventoryAction.RESET -> it.quantity
                    }
                })
    }

}
