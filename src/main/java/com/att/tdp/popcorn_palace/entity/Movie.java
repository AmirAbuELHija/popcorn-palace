package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "genre cannot be empty")
    @Column(nullable = false)
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 300, message = "Duration cannot exceed 300 minutes")
    @Column(nullable = false)
    private int duration;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating cannot exceed 10")
    @Column(nullable = false)
    private double rating;

    @Min(value = 1900, message = "Release year must be after 1900")
    @Max(value = 2025, message = "Release year cannot exceed 2100")
    @Column(nullable = false)
    private int releaseYear;
}

