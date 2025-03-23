package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingResponseDTO;
import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    private MockMvc mockMvc;
    private Booking booking;
    private UUID bookingId;

    @BeforeEach//start before the tests
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        bookingId = UUID.randomUUID();
        booking = new Booking(bookingId, 1L, 15, "84438967-f68f-4fa0-b620-0f08217e76af");
    }

    // test for a Successful booking
    @Test
    void bookTicket_Successful_Returns200() throws Exception {
        when(bookingService.bookTicket(any(Booking.class))).thenReturn(new BookingResponseDTO(bookingId));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "showtimeId": 1,
                    "seatNumber": 15,
                    "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
                }
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.bookingId").value(bookingId.toString()));
    }

    //Invalid UUID test
    @Test
    void bookTicket_InvalidUUID_Returns400() throws Exception {
        when(bookingService.bookTicket(any(Booking.class))).thenThrow(new InvalidInputException("Invalid UUID format for userId."));
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "showtimeId": 1,
                    "seatNumber": 15,
                    "userId": "invalid-uuid"
                }
                """)).andExpect(status().isBadRequest());
    }

    //invalid Booking for Past Showtime test
    @Test
    void bookTicket_PastShowtime_Returns400() throws Exception {
        when(bookingService.bookTicket(any(Booking.class))).thenThrow(new InvalidInputException("Cannot book a seat for a past showtime."));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "showtimeId": 1,
                    "seatNumber": 15,
                    "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
                }
                """)).andExpect(status().isBadRequest());
    }

    //Booking for Non-Existing Showtime
    @Test
    void bookTicket_NonExistingShowtime_Returns404() throws Exception {
        when(bookingService.bookTicket(any(Booking.class))).thenThrow(new ResourceNotFoundException("Showtime ID 100 does not exist."));
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "showtimeId": 100,
                    "seatNumber": 15,
                    "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
                }
                """)).andExpect(status().isNotFound());
    }

    //Duplicate seat Booking (Seat Already Taken)
    @Test
    void bookTicket_SeatAlreadyBooked_Returns409() throws Exception {
        when(bookingService.bookTicket(any(Booking.class))).thenThrow(new DataConflictException("Seat number 15 is already booked."));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "showtimeId": 1,
                    "seatNumber": 15,
                    "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
                }
                """)).andExpect(status().isConflict());
    }

    //Cancel Booking Successfully
    @Test
    void cancelBooking_Success_Returns200() throws Exception {
        doNothing().when(bookingService).cancelBooking(any(UUID.class));
        mockMvc.perform(delete("/bookings/" + bookingId)).andExpect(status().isOk());
    }

    //Cancel Non-Existing Booking
    @Test
    void cancelBooking_NonExistingBooking_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found with ID: " + bookingId)).when(bookingService).cancelBooking(any(UUID.class));
        mockMvc.perform(delete("/bookings/" + bookingId)).andExpect(status().isNotFound());
    }

    //Cancel Booking with Invalid UUID
    @Test
    void cancelBooking_InvalidUUID_Returns400() throws Exception {
        mockMvc.perform(delete("/bookings/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}
