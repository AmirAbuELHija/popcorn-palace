package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.dto.BookingResponseDTO;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    private Booking booking;
    private Showtime showtime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        showtime = new Showtime();
        showtime.setId(1L);
        showtime.setStartTime(LocalDateTime.now().plusDays(1));
        showtime.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        booking = new Booking();
        booking.setShowtimeId(1L);
        booking.setSeatNumber(10);
        booking.setUserId(UUID.randomUUID().toString());
    }
    // valid input testing (booking a ticket for future showtime)
    @Test
    void bookTicket_SuccessfulBooking_ReturnsBookingResponseDTO() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 10)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingResponseDTO response = bookingService.bookTicket(booking);
        UUID bookingId = response.bookingId();
        assertNotNull(response);
        assertEquals(booking.getBookingId(), bookingId);
    }

    @Test
    void bookTicket_InvalidUUID_ThrowsInvalidInputException() {
        booking.setUserId("invalid-uuid");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.bookTicket(booking));
        assertEquals("Invalid UUID format for userId: invalid-uuid", exception.getMessage());
    }


    @Test
    void bookTicket_NonExistingShowtime_ThrowsResourceNotFoundException() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> bookingService.bookTicket(booking));
        assertEquals("Showtime ID 1 does not exist.", exception.getMessage());
    }

    //check if the booking times is after the showtim time range (it means the show has ended) according to our local time for execution
    @Test
    void bookTicket_PastShowtime_ThrowsInvalidInputException() {
        showtime.setStartTime(LocalDateTime.now().minusDays(1));
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.bookTicket(booking));
        assertEquals("Cannot book a seat for a past showtime.", exception.getMessage());
    }

    //check if seat already booked for the same theater and showtime
    @Test
    void bookTicket_SeatAlreadyBooked_ThrowsDataConflictException() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(1L, 10)).thenReturn(true);
        DataConflictException exception = assertThrows(DataConflictException.class, () -> bookingService.bookTicket(booking));
        assertEquals("Seat number 10 is already booked for showtime ID 1", exception.getMessage());
    }
}
