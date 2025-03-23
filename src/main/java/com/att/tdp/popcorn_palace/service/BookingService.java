package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.dto.BookingResponseDTO;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    // Book a Ticket
    @Transactional
    public BookingResponseDTO bookTicket(Booking booking) {
        if (booking == null) {//for testing
            throw new InvalidInputException("Booking details cannot be null.");
        }
        //check if userId is UUId format
        if (!isValidUUID(booking.getUserId())) {
            throw new InvalidInputException("Invalid UUID format for userId: " + booking.getUserId());
        }

        // check if the showtime is in the database
        Showtime showtime = showtimeRepository.findById(booking.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime ID " + booking.getShowtimeId() + " does not exist."));

        //check if trying to book a past showtime according to the localtime.now() (our current time)
        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidInputException("Cannot book a seat for a past showtime.");
        }

        //check for double booked seat
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(booking.getShowtimeId(), booking.getSeatNumber())) {
            throw new DataConflictException("Seat number " + booking.getSeatNumber() +
                    " is already booked for showtime ID " + booking.getShowtimeId());}

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingResponseDTO(savedBooking.getBookingId());
    }

    //UUID Validation
    private boolean isValidUUID(String userId) {
        try {
            UUID.fromString(userId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    // Get Booking by ID (for testing)
    public Booking getBookingById(UUID bookingId) {

        return bookingRepository.findById(bookingId)
                //check if the bookingId exists
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
    }

    //Cancel a Booking (for testing)
    @Transactional
    public void cancelBooking(UUID bookingId) {
        //check if the booking id exists
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found with ID: " + bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }
}
