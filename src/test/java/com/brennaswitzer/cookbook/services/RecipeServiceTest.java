package com.brennaswitzer.cookbook.services;

import com.brennaswitzer.cookbook.domain.Task;
import com.brennaswitzer.cookbook.domain.TaskList;
import com.brennaswitzer.cookbook.domain.User;
import com.brennaswitzer.cookbook.repositories.TaskListRepository;
import com.brennaswitzer.cookbook.repositories.UserRepository;
import com.brennaswitzer.cookbook.util.RecipeBox;
import com.brennaswitzer.cookbook.util.WithAliceBobEve;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Iterator;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@WithAliceBobEve
public class RecipeServiceTest {

    @Autowired
    private RecipeService service;

    @Autowired
    private TaskListRepository listRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User alice;

    @Before
    public void setUp() {
        alice = userRepository.getByName("Alice");
    }

    @Test
    public void addRawIngredientsToList() {
        RecipeBox box = new RecipeBox();
        box.persist(entityManager);

        TaskList list = listRepo.save(new TaskList(alice, "Groceries"));
        assertEquals(0, list.getSubtaskCount());
        Consumer<Iterator<Task>> checkItems = itr -> {
            assertEquals("2 c flour", itr.next().getName());
            assertEquals("1 c water", itr.next().getName());
            assertEquals("1 packet yeast", itr.next().getName());
            assertEquals("1 Tbsp sugar", itr.next().getName());
        };

        service.addRawIngredientsToList(box.pizzaCrust, list, true);
        assertEquals(1 + 4, list.getSubtaskCount());
        Iterator<Task> itr = list.getOrderedSubtasksView().iterator();
        assertEquals("Pizza Crust:", itr.next().getName());
        checkItems.accept(itr);
        assertFalse(itr.hasNext());

        service.addRawIngredientsToList(box.pizzaCrust, list, false);
        assertEquals(1 + 4 + 4, list.getSubtaskCount());
        itr = list.getOrderedSubtasksView().iterator();
        assertEquals("Pizza Crust:", itr.next().getName());
        checkItems.accept(itr);
        checkItems.accept(itr);
        assertFalse(itr.hasNext());
    }

    @Test
    public void addPurchasableSchmankiesToList() {
        RecipeBox box = new RecipeBox();
        box.persist(entityManager);

        TaskList list = listRepo.save(new TaskList(alice, "Groceries"));
        assertEquals(0, list.getSubtaskCount());
        Consumer<Iterator<Task>> checkItems = itr -> {
            // pizza
            assertEquals("pepperoni (4 oz)", itr.next().getName());
            // sauce
            assertEquals("fresh tomatoes (1 lbs)", itr.next().getName());
            assertEquals("tomato paste (1 (6 oz) can)", itr.next().getName());
            assertEquals("italian seasoning", itr.next().getName());
            assertEquals("salt (1 tsp, 0.5 tsp)", itr.next().getName());
            // crust
            assertEquals("flour (2 c)", itr.next().getName());
            assertEquals("water (1 c)", itr.next().getName());
            assertEquals("yeast (1 packet)", itr.next().getName());
            assertEquals("sugar (1 Tbsp)", itr.next().getName());
        };

        service.addPurchasableSchmankiesToList(box.pizza, list, false);
        assertEquals(9, list.getSubtaskCount());
        Iterator<Task> itr = list.getOrderedSubtasksView().iterator();
        checkItems.accept(itr);
        assertFalse(itr.hasNext());

        service.addPurchasableSchmankiesToList(box.pizza, list, true);
        assertEquals(9 + 1 + 9, list.getSubtaskCount());
        itr = list.getOrderedSubtasksView().iterator();
        checkItems.accept(itr);
        assertEquals("Pizza:", itr.next().getName());
        checkItems.accept(itr);
        assertFalse(itr.hasNext());
    }

}