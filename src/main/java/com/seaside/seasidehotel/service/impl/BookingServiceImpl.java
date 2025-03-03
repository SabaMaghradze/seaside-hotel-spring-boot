package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.exception.InvalidBookingRequestException;
import com.seaside.seasidehotel.exception.ResourceNotFoundException;
import com.seaside.seasidehotel.model.Booking;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.repository.BookingRepository;
import com.seaside.seasidehotel.repository.RoomRepository;
import com.seaside.seasidehotel.service.BookingService;
import com.seaside.seasidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    @Override
    public List<Booking> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Couldn't find the booking with confirmation code: " + confirmationCode));
    }

    @Override
    public String saveBooking(Long roomId, Booking booking) {

        LocalDate today = LocalDate.now();

        if (booking.getCheckInDate().isBefore(today)) {
            throw new InvalidBookingRequestException("Check-in date cannot be in the past");
        }

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }

        Room room = roomService.getRoomById(roomId);
        List<Booking> existingBookings = room.getBookings();
        boolean isRoomAvailable = roomIsAvailable(booking, existingBookings);
        if (isRoomAvailable) {
            room.addBooking(booking);
            bookingRepository.save(booking);
        } else {
            throw new InvalidBookingRequestException("The room is already booked for the selected dates");
        }
        return booking.getConfirmationCode();
    }

    private boolean roomIsAvailable(Booking booking, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->

                        booking.getCheckInDate().equals(existingBooking.getCheckInDate())

                                || booking.getCheckOutDate().isBefore(existingBooking.getCheckOutDate()) // controversial

                                || (booking.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && booking.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))

                                || (booking.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && booking.getCheckOutDate().equals(existingBooking.getCheckOutDate()))

                                || (booking.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                                && booking.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (booking.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && booking.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (booking.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && booking.getCheckOutDate().equals(booking.getCheckInDate()))
                );
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getBookingsByUserEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

}
