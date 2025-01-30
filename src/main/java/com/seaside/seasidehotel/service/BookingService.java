package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.Booking;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    List<Booking> getAllBookingsByRoomId(Long id);
}
