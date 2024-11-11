package com.spring.backend.Repository;

import com.spring.backend.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("select b from Booking b where b.bookingConfirmation=?1 ")
    Optional<Booking> findByBookingConfirmation(String bookingConfirmationCode);


    @Query("select b from Booking b where b.room.id = ?1")
    List<Booking> findByRoomId(Long roomId);

    @Query("select b from Booking b where b.user.id = ?1")
    List<Booking> findByUserId(String userId);


}
