package com.seaside.seasidehotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull(message = "Check-in date is required") // Because @NotBlank is only applicable to Strings.
    @Column(name = "check_in")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @NotBlank(message = "Guest name is required")
    @Column(name = "name")
    private String guestFullName;

    @NotBlank(message = "Email is required")
    @Column(name = "email")
    private String guestEmail;

    @Min(value = 1, message = "At least 1 adult is required")
    @Column(name = "adults")
    private int numOfAdults;

    @Column(name = "children")
    private int numOfChildren;

    @Column(name = "total_guests")
    private int totalGuests;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateTotalGuests() {
        this.totalGuests = this.numOfAdults + this.numOfChildren;
    }

    public void setNumOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalGuests();
    }

    public void setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalGuests();
    }

}

















