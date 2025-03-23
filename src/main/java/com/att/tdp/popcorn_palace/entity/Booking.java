package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @Column(nullable = false, name = "showtime_id")
    private Long showtimeId;

    @Min(value = 1, message = "Seat number must be at least 1")
    @Column(nullable = false)
    private int seatNumber;

    @NotBlank(message = "User ID cannot be empty")
    @Column(nullable = false)
    private String userId;
}
