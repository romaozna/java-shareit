package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> getBookingsByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc ")
    List<Booking> getCurrentBookingsByBooker(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> getPastBookingsByBooker(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> getFutureBookingsByBooker(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and b.status = 'WAITING' and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> getWaitingBookingsByBooker(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.booker.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> getRejectedBookingsByBooker(Long bookerId, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc ")
    List<Booking> getAllBookingsByOwner(Long ownerId, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 and ?2 between b.start and b.end " +
            "order by b.start desc ")
    List<Booking> getCurrentBookingsByOwner(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> getPastBookingsByOwner(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> getFutureBookingsByOwner(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = 'WAITING' and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> getWaitingBookingsByOwner(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(" select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> getRejectedBookingsByOwner(Long ownerId, Pageable pageable);

    @Query(value = " select * from bookings b join items i on i.id = b.item_id " +
            "where b.item_id = ?1 and b.start_date < ?2 " +
            "order by b.end_date desc " +
            "limit 1 ", nativeQuery = true)
    Optional<Booking> getLastBooking(Long itemId, LocalDateTime now);

    @Query(value = " select * from bookings b join items i on i.id = b.item_id " +
            "where b.item_id = ?1 and b.start_date > ?2 and b.status != 'REJECTED'" +
            "order by b.start_date asc " +
            "limit 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(Long itemId, LocalDateTime now);

    @Query(" select b from Booking b join Item i on i.id = b.item.id " +
            "where b.booker.id = ?1 and i.id = ?2 and b.status = 'APPROVED' and b.end < ?3 ")
    List<Booking> getAllPastAndApprovedUserBooking(Long bookerId, Long itemId, LocalDateTime now);
}
