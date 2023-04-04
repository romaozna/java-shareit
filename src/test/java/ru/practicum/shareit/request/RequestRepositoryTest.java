package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    private ItemRequest request;
    private ItemRequest anotherRequest;

    @BeforeEach
    void setup() {
        User user = new User(
                null,
                "Roman",
                "roman@mail.com");

        User anotherUser = new User(
                null,
                "Max",
                "Max@mail.com");

        request = new ItemRequest(
                null,
                "request",
                1L,
                LocalDateTime.now(),
                new ArrayList<>());

        anotherRequest = new ItemRequest(
                null,
                "another request",
                2L,
                LocalDateTime.now().plusDays(1),
                new ArrayList<>());

        entityManager.persist(user);
        entityManager.persist(anotherUser);
        entityManager.flush();
    }

    @Test
    void createRequestTest() {
        ItemRequest createdRequest = requestRepository.save(request);

        Assertions.assertNotNull(createdRequest);
        Assertions.assertEquals(1L, createdRequest.getId());
        Assertions.assertEquals(request.getDescription(), createdRequest.getDescription());
        Assertions.assertEquals(request.getRequesterId(), createdRequest.getRequesterId());
        Assertions.assertEquals(request.getCreated(), createdRequest.getCreated());
    }

    @Test
    void findRequestByIdTest() {
        entityManager.persist(request);
        entityManager.flush();

        ItemRequest found = requestRepository.findById(1L).orElse(null);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getId());
        Assertions.assertEquals(request.getDescription(), found.getDescription());
        Assertions.assertEquals(request.getRequesterId(), found.getRequesterId());
        Assertions.assertEquals(request.getCreated(), found.getCreated());
    }

    @Test
    void findAllByRequesterIdNotOrderByCreatedAscTest() {
        entityManager.persist(request);
        entityManager.persist(anotherRequest);
        entityManager.flush();

        Long user2Id = 2L;
        Long user1Id = 1L;
        List<ItemRequest> requests = requestRepository
                .findAllByRequesterIdNotOrderByCreatedAsc(user2Id, PageRequest.of(0, 1));

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(1L, requests.get(0).getId());
        Assertions.assertEquals(request.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(request.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(user1Id, requests.get(0).getRequesterId());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedAscTest() {
        entityManager.persist(request);
        entityManager.persist(anotherRequest);
        entityManager.flush();

        Long user2Id = 2L;
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedAsc(user2Id);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(2L, requests.get(0).getId());
        Assertions.assertEquals(anotherRequest.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(anotherRequest.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(user2Id, requests.get(0).getRequesterId());
    }
}
