package com.brennaswitzer.cookbook.services

import com.brennaswitzer.cookbook.domain.PantryItem
import com.brennaswitzer.cookbook.message.IngredientMessage
import com.brennaswitzer.cookbook.payload.IngredientInfo.Companion.from
import com.brennaswitzer.cookbook.repositories.PantryItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.StreamSupport
import javax.transaction.Transactional

@Service
@Transactional
class UpdatePantryItemStoreOrder {

    @Autowired
    lateinit var pantryItemRepository: PantryItemRepository

    @Autowired
    lateinit var messagingTemplate: SimpMessageSendingOperations

    fun invoke(id: Long, targetId: Long, after: Boolean) {
        val seq = AtomicInteger(0)
        val active = pantryItemRepository.findById(id).get()
        StreamSupport.stream(
            pantryItemRepository.findAll().spliterator(),
            false
        )
            .sorted(PantryItem.BY_STORE_ORDER)
            .forEachOrdered {
                if (it.id == active.id) return@forEachOrdered
                if (it.id == targetId) {
                    if (after) {
                        ensureStoreOrder(it, seq.incrementAndGet())
                        ensureStoreOrder(active, seq.incrementAndGet())
                    } else {
                        ensureStoreOrder(active, seq.incrementAndGet())
                        ensureStoreOrder(it, seq.incrementAndGet())
                    }
                } else {
                    ensureStoreOrder(it, seq.incrementAndGet())
                }
            }
    }

    private fun ensureStoreOrder(it: PantryItem, order: Int) {
        if (it.storeOrder == order) return
        println("set $it to $order")
        it.storeOrder = order
        val m = IngredientMessage()
        m.type = "update"
        m.id = it.id
        m.info = from(it)
        messagingTemplate.convertAndSend("/topic/pantry-items", m)
    }
}
