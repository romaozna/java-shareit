package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemStorageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemStorage itemStorage;

    private final User user = new User(
            null,
            "Roman",
            "roman@mail.com");

    private final Item item = new Item(
            null,
            "Brush",
            "Brush for wash",
            true,
            user,
            1L);

    private final ItemRequest request = new ItemRequest(
            null,
            "request",
            1L,
            LocalDateTime.now(),
            new ArrayList<>());

    @BeforeEach
    void setup() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.flush();
    }

    @Test
    void createItem() {
        Item found = itemStorage.save(item);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getId());
        Assertions.assertEquals(item.getName(), found.getName());
        Assertions.assertEquals(item.getDescription(), found.getDescription());
        Assertions.assertTrue(found.getAvailable());
        Assertions.assertNotNull(found.getRequestId());
        Assertions.assertEquals(user.getName(), found.getOwner().getName());
        Assertions.assertEquals(user.getEmail(), found.getOwner().getEmail());
    }

    @Test
    void findItemById() {
        entityManager.persist(item);
        entityManager.flush();

        Item found = itemStorage.findById(1L).orElse(null);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getId());
        Assertions.assertEquals(item.getName(), found.getName());
        Assertions.assertEquals(item.getDescription(), found.getDescription());
        Assertions.assertTrue(found.getAvailable());
        Assertions.assertNotNull(found.getRequestId());
        Assertions.assertEquals(user.getName(), found.getOwner().getName());
        Assertions.assertEquals(user.getEmail(), found.getOwner().getEmail());
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemStorage.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(item.getName(), found.get(0).getName());
        Assertions.assertEquals(item.getDescription(), found.get(0).getDescription());
        Assertions.assertTrue(found.get(0).getAvailable());
        Assertions.assertNotNull(found.get(0).getRequestId());
        Assertions.assertEquals(user.getName(), found.get(0).getOwner().getName());
        Assertions.assertEquals(user.getEmail(), found.get(0).getOwner().getEmail());
    }

    @Test
    void search() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemStorage.search("Brush for wash", PageRequest.of(0, 1));

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(1L, found.get(0).getId());
        Assertions.assertEquals(item.getName(), found.get(0).getName());
        Assertions.assertEquals(item.getDescription(), found.get(0).getDescription());
        Assertions.assertTrue(found.get(0).getAvailable());
        Assertions.assertNotNull(found.get(0).getRequestId());
        Assertions.assertEquals(user.getName(), found.get(0).getOwner().getName());
        Assertions.assertEquals(user.getEmail(), found.get(0).getOwner().getEmail());
    }
}