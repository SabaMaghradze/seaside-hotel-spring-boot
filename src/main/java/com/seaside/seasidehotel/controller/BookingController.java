package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.exception.InvalidBookingRequestException;
import com.seaside.seasidehotel.exception.ResourceNotFoundException;
import com.seaside.seasidehotel.model.Booking;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.response.BookingResponse;
import com.seaside.seasidehotel.response.RoomResponse;
import com.seaside.seasidehotel.service.BookingService;
import com.seaside.seasidehotel.service.RoomService;
import com.seaside.seasidehotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/all-bookings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        List<Booking> allBookings = bookingService.getAllBookings();
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : allBookings) {
            BookingResponse response = getBookingResponse(booking);
            responses.add(response);
        }
        return ResponseEntity.ok(responses);
    }

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

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @Valid @RequestBody Booking booking) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, booking);
            return ResponseEntity.ok
                    ("Room has been booked successfully, your confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookingId}/delete")
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

    @GetMapping("/user/{userEmail}/bookings")
    public ResponseEntity<?> getBookingsByUserEmail(@PathVariable String userEmail) {

        List<Booking> bookings = bookingService.getBookingsByUserEmail(userEmail);
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookings) {
            BookingResponse response = getBookingResponse(booking);
            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }
}











