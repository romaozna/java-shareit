package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @BeforeEach
    void initVarsForTests() {
        ItemRequest request = new ItemRequest(
                null,
                "request",
                1L,
                LocalDateTime.now(),
                new ArrayList<>());

        user = new User(
                null,
                "Roman",
                "roman@mail.com");

        item = new Item(
                null,
                "Brush",
                "Brush for wash",
                true,
                user,
                1L);

        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.flush();
    }

    @Test
    void createItemTest() {
        Item found = itemRepository.save(item);

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
    void findItemByIdTest() {
        entityManager.persist(item);
        entityManager.flush();

        Item found = itemRepository.findById(1L).orElse(null);

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
    void findAllByOwnerIdOrderByIdAscTest() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1));

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
    void searchTest() {
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemRepository.search("Brush for wash", PageRequest.of(0, 1));

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