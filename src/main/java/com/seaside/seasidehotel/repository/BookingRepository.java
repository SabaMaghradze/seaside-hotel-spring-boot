package com.seaside.seasidehotel.repository;

import com.seaside.seasidehotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByConfirmationCode(String confirmationCode);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByGuestEmail(String email);
}
