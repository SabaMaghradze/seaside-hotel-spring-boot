package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.exception.PhotoRetrievalException;
import com.seaside.seasidehotel.model.Booking;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.response.BookingResponse;
import com.seaside.seasidehotel.response.RoomResponse;
import com.seaside.seasidehotel.service.BookingService;
import com.seaside.seasidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    private RoomResponse getRoomResponse(Room room) {
        List<Booking> bookings = bookingService.getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingsInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getConfirmationCode()))
                .toList();

        byte[] photo = null;
        Blob roomPhoto = room.getPhoto();

        if (roomPhoto != null) {
            try {
                photo = roomPhoto.getBytes(1, (int) roomPhoto.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), bookingsInfo, photo);
    }

    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam(value = "picture", required = false) MultipartFile pic,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        Room saveRoom = roomService.addNewRoom(pic, roomType, roomPrice);

        RoomResponse response = new RoomResponse(saveRoom.getId(), saveRoom.getRoomType(), saveRoom.getRoomPrice());

        // removable
        if (pic != null && !pic.isEmpty()) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(saveRoom.getId());
            String base64Photo = Base64.encodeBase64String(photoBytes);
            response.setPhoto(base64Photo);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room-types")
    public Set<String> getAllRoomTypes() {
        Set<String> roomTypes = roomService.getAllRoomTypes();
        System.out.println("Room types: " + roomTypes);
        return roomTypes;
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {

        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : rooms) {
            RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                roomResponse.setPhoto(base64Photo);
            }
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("/deleteRoom/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable("roomId") Long roomId) {

        Map<String, Object> response = new HashMap<>();

        try {
            roomService.deleteRoom(roomId);
            response.put("message", "success");
            response.put("roomId", roomId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "error");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long roomId) {

        Room room = roomService.getRoomById(roomId);

        RoomResponse response = getRoomResponse(room);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse>
    updateRoom(@PathVariable Long roomId,
               @RequestParam(required = false) String roomType,
               @RequestParam(required = false) BigDecimal roomPrice,
               @RequestParam(value = "picture", required = false) MultipartFile pic) throws SQLException, IOException {

        Room room = roomService.updateRoom(roomId, roomType, roomPrice, pic);

        RoomResponse response = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());

        byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
        if (photoBytes != null && photoBytes.length > 0) {
            String base64Photo = Base64.encodeBase64String(photoBytes);
            response.setPhoto(base64Photo);
        }

        return ResponseEntity.ok(response);

    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(value = "roomType", required = false) String roomType) throws SQLException {

        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> responses = new ArrayList<>();

        for (Room room : availableRooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse response = getRoomResponse(room);
                response.setPhoto(base64Photo);
                responses.add(response);
            }
        }
        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        System.out.println(checkInDate);
        System.out.println(checkOutDate);
        return ResponseEntity.ok(responses);
    }
}























