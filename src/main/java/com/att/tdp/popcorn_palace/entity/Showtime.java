package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movieId;

    @NotBlank(message = "Theater name cannot be empty")
    @Column(nullable = false)
    private String theater;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Min(value = 1, message = "Price must be at least 1.0")
    @Max(value = 100, message = "Price cannot exceed 100.0")
    @Column(nullable = false)
    private double price;
}
