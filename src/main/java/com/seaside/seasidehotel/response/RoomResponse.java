package com.seaside.seasidehotel.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomResponse {

    private Long id;

    private String roomType;

    private BigDecimal roomPrice;

    private String photo;

    private boolean isBooked = false;

    private List<BookingResponse> bookings;

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice,
                        boolean isBooked, List<BookingResponse> bookings, byte[] photoBytes) {

        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;

        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;

//        String encodedPhoto = Base64.encodeBase64String(photoBytes);
//        System.out.println("Encoded Photo: " + encodedPhoto);
//        System.out.println("photo: " + photo);

//        byte[] decodedPhoto = Base64.decodeBase64(encodedPhoto);
//        System.out.println("Decoded Photo: " + decodedPhoto);
//        System.out.println("photo bytes: " + photoBytes);

        this.isBooked = isBooked;
        this.bookings = bookings;
    }
}
