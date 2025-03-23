package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingResponseDTO;
import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Book a Ticket
    @PostMapping
    public ResponseEntity<BookingResponseDTO> bookTicket(@Valid @RequestBody Booking booking) {
        BookingResponseDTO response = bookingService.bookTicket(booking);
        return ResponseEntity.ok(response);
    }


    // Get Booking by ID (for testing)
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    // Cancel a Booking (for testing)
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }
}

