package com.seaside.seasidehotel.response;

import com.seaside.seasidehotel.model.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long bookingId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String guestFullName;

    private String guestEmail;

    private int numOfAdults;

    private int numOfChildren;

    private int totalGuests;

    private String confirmationCode;

    private RoomResponse room;

    public BookingResponse(Long bookingId, LocalDate checkInDate, LocalDate checkOutDate,
                           String confirmationCode) {

        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.confirmationCode = confirmationCode;
    }
}
