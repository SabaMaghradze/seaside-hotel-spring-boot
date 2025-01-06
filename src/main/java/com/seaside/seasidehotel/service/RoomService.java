package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@Service
public interface RoomService {
    Room addNewRoom(MultipartFile pic, String roomType, BigDecimal roomPrice) throws SQLException, IOException;
}
