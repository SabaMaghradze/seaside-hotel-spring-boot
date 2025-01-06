package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.model.Room;
import com.seaside.seasidehotel.response.RoomResponse;
import com.seaside.seasidehotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("picture")MultipartFile pic,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice")BigDecimal roomPrice) throws SQLException, IOException {

        Room saveRoom = roomService.addNewRoom(pic, roomType, roomPrice);

        RoomResponse response = new RoomResponse(saveRoom.getId(), saveRoom.getRoomType(), saveRoom.getRoomPrice());

        return ResponseEntity.ok(response);
    }
}
