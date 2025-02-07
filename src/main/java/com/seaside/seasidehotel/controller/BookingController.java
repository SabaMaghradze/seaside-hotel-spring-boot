package com.seaside.seasidehotel.controller;


import com.seaside.seasidehotel.exception.InvalidBookingRequestException;
import com.seaside.seasidehotel.exception.ResourceNotFoundException;
import com.seaside.seasidehotel.model.Booking;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.response.BookingResponse;
import com.seaside.seasidehotel.response.RoomResponse;
import com.seaside.seasidehotel.service.BookingService;
import com.seaside.seasidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    private BookingResponse getBookingResponse(Booking booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId());

        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());

        return new BookingResponse(booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalGuests(),
                booking.getConfirmationCode(), roomResponse);
    }

    @CrossOrigin(origins = "http://localhost:5127")
    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        List<Booking> allBookings = bookingService.getAllBookings();
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : allBookings) {
            BookingResponse response = new BookingResponse(booking.getBookingId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getConfirmationCode());
            responses.add(response);
        }
        return ResponseEntity.ok(responses);
    }

    @CrossOrigin(origins = "http://localhost:5172")
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            Booking booking = bookingService.findByConfirmationCode(confirmationCode);
            BookingResponse response = getBookingResponse(booking);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:5127")
    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody Booking booking) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, booking);
            return ResponseEntity.ok
                    ("Room has been booked successfully, your confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Long bookingId) {

        Map<String, Object> response = new HashMap<>();

        try {
            bookingService.cancelBooking(bookingId);
            response.put("message", "success");
            response.put("bookingId", bookingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "error");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}











