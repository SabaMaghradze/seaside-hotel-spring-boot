package com.seaside.seasidehotel.service.impl;
import com.seaside.seasidehotel.exception.ResourceNotFoundException;
import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.repository.RoomRepository;
import com.seaside.seasidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile pic, String roomType, BigDecimal roomPrice)
            throws SQLException, IOException {

        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (pic != null && !pic.isEmpty()) {
            byte[] bytes = pic.getBytes();
            Blob photoBlob = new SerialBlob(bytes);
            room.setPhoto(photoBlob);
        }

        return roomRepository.save(room);
    }

    @Override
    public Set<String> getAllRoomTypes() {

        List<Room> allRooms = roomRepository.findAll();
        Set<String> allRoomTypes = new HashSet<>();

        for (Room room : allRooms) {
            allRoomTypes.add(room.getRoomType());
        }

        return allRoomTypes;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Failed to find the room with id " + roomId));

        Blob photoBlob = room.getPhoto();
        if (photoBlob == null) {
            return new byte[0];
        }
        return photoBlob.getBytes(1, (int) photoBlob.length());
    }

    @Override
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Failed to find the room with id " + roomId));

        roomRepository.delete(room);
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile pic) throws IOException, SQLException {

        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isEmpty()) {
            throw new ResourceNotFoundException("Room not found with id " + roomId);
        }

        Room room = optionalRoom.get();

        if (roomType != null) {
            room.setRoomType(roomType);
        }

        room.setRoomPrice(roomPrice);

        if (pic != null && !pic.isEmpty()) {
            byte[] bytes = pic.getBytes();
            Blob photoBlob = new SerialBlob(bytes);
            room.setPhoto(photoBlob);
        } else {
            room.setPhoto(null);
        }

        return roomRepository.save(room);
    }
}










































