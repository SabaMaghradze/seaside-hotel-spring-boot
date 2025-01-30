package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.model.Booking;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.repository.BookingRepository;
import com.seaside.seasidehotel.repository.RoomRepository;
import com.seaside.seasidehotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getAllBookingsByRoomId(Long id) {

        Optional<Room> theRoom = roomRepository.findById(id);
        List<Booking> bookings = null;

        if (!ObjectUtils.isEmpty(theRoom)) {
            bookings = theRoom.get().getBookings();
        }

        return bookings;
    }

}
