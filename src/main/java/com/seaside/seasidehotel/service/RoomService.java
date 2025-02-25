
package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public interface RoomService {
    Room addNewRoom(MultipartFile pic, String roomType, BigDecimal roomPrice) throws SQLException, IOException;
    Set<String> getAllRoomTypes();
    List<Room> getAllRooms();
    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;
    void deleteRoom(Long roomId);
    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile pic) throws IOException, SQLException;
    Room getRoomById(Long roomId);
    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) throws SQLException;
}
