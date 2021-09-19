package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.repositories.InventoryItemRepository
import com.brennaswitzer.cookbook.repositories.InventoryTxRepository
import com.brennaswitzer.cookbook.util.RecipeBox
import com.brennaswitzer.cookbook.util.UserPrincipalAccess
import com.brennaswitzer.cookbook.util.WithAliceBobEve
import com.brennaswitzer.cookbook.util.printResultSet
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
@WithAliceBobEve
class InventoryDatabaseTest {

    @Autowired
    lateinit var itemRepo: InventoryItemRepository

    @Autowired
    lateinit var txRepo: InventoryTxRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var principalAccess: UserPrincipalAccess

    @Autowired
    lateinit var jdbcTmpl: JdbcTemplate

    @Test
    fun doesItSmoke() {
        val box = RecipeBox()
        box.persist(entityManager, principalAccess.user)

        val salt = itemRepo.save(
            InventoryItem(
                principalAccess.user,
                box.salt,
            )
        )

        // I _seriously_ bought salt
        salt.acquire(Quantity(123456, box.cup))

        // bought some more in here too, but didn't write it down
        (4..19).forEach {
            salt.consume(Quantity(it, box.tbsp))
        }

        // I just checked; I have 3 cups of salt.
        salt.reset(Quantity(3, box.cup))

        // used 4 Tbsp (half cup)
        salt.consume(Quantity(4, box.tbsp))

        checkSalt(salt)

        entityManager.flush()
        entityManager.clear()
        assertEquals(1, itemRepo.count())
        assertEquals(19, txRepo.count())

        checkSalt(itemRepo.findById(salt.id!!).get())

        jdbcTmpl.query(
            "select * from inventory_tx order by created_at, id",
            ::printResultSet
        )
    }

    private fun checkSalt(salt: InventoryItem) {
        assertEquals(19, salt.txCount)
        assertEquals(19, salt.transactions.size)
        assertEquals(
            salt.quantity,
            txRepo.findByItem(
                salt,
                Sort.by(
                    Sort.Order.asc(InventoryTx_.CREATED_AT),
                    Sort.Order.asc(InventoryTx_.ID)
                )
            )
                .fold(Quantity.ZERO) { total, it ->
                    println(it)
                    it.computeNewQuantity(total)
                })
    }

}
