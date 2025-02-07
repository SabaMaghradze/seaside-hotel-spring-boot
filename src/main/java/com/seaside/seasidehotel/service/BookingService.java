package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.Booking;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookingsByRoomId(Long bookingId);
    List<Booking> getAllBookings();
    Booking findByConfirmationCode(String confirmationCode);
    String saveBooking(Long roomId, Booking booking);
    void cancelBooking(Long bookingId);
}
