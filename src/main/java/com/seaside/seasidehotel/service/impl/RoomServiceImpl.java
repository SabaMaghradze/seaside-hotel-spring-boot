package com.seaside.seasidehotel.service.impl;

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

        if (!pic.isEmpty()) {
            byte[] bytes = pic.getBytes();
            Blob photoBlob = new SerialBlob(bytes);
            room.setPhoto(photoBlob);
        }

        return roomRepository.save(room);
    }
}
