package com.brennaswitzer.cookbook.services

import com.brennaswitzer.cookbook.domain.PantryItem
import com.brennaswitzer.cookbook.message.IngredientMessage
import com.brennaswitzer.cookbook.repositories.PantryItemRepository
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.springframework.messaging.simp.SimpMessageSendingOperations
import java.util.*

internal class UpdatePantryItemStoreOrderTest {

    @Test
    fun doesItSmoke() {
        val repo = mockk<PantryItemRepository>()
        val items = listOf(1L, 2, 4, 6).map { itemForId(it) }
        val itemsById = items.associateBy { it.id }
        every { repo.findById(any()) } answers {
            Optional.ofNullable(itemsById[arg<Long>(0)])
        }
        every { repo.findAll() } answers {
            items
        }

        val msgTmpl = mockk<SimpMessageSendingOperations>()
        val msgDest = "/topic/pantry-items"
        val messages = mutableListOf<IngredientMessage>()
        every { msgTmpl.convertAndSend(msgDest, capture(messages)) } just Runs

        val svc = UpdatePantryItemStoreOrder()
        svc.pantryItemRepository = repo
        svc.messagingTemplate = msgTmpl

        svc.invoke(2, 4, true)

        verify(exactly = 1) {
            repo.findById(2)
            repo.findAll()
        }
        println(items.map { "$it:${it.storeOrder}" })
        assertEquals(3, messages.size)
        val messagesById = messages.associateBy { it.id }
        listOf(1L, 4, 2, 6).forEachIndexed { i, id ->
            val it = itemsById[id]!!
            assertEquals(i + 1, it.storeOrder)
            val msg = messagesById[id]
            if (msg != null)
                assertEquals(i + 1, msg.info?.storeOrder)
        }
    }

    private fun itemForId(id: Long): PantryItem {
        val item = PantryItem("item $id")
        item.id = id
        item.storeOrder = id.toInt()
        return item
    }

}
