package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ShowtimeControllerTest {

    @InjectMocks
    private ShowtimeController showtimeController;

    @Mock
    private ShowtimeService showtimeService;

    private MockMvc mockMvc;
    private Showtime showtime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(showtimeController).build();

        showtime = new Showtime(1L, 2L, "Main Theater",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                20.0);
    }

    //Add Showtime
    @Test
    void addShowtime_Success_Returns200() throws Exception {
        when(showtimeService.addShowtime(any(Showtime.class))).thenReturn(showtime);

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "movieId": 2,
                    "theater": "Main Theater",
                    "startTime": "2025-04-01T14:00:00",
                    "endTime": "2025-04-01T16:00:00",
                    "price": 20.0
                }
                """))
                .andExpect(status().isOk());
    }

    //Add Showtime with Overlapping Time
    @Test
    void addShowtime_OverlappingTime_Returns409() throws Exception {
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new DataConflictException("Showtime conflicts with an existing showtime."));

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "movieId": 2,
                    "theater": "Main Theater",
                    "startTime": "2025-04-01T14:00:00",
                    "endTime": "2025-04-01T16:00:00",
                    "price": 20.0
                }
                """))
                .andExpect(status().isConflict());
    }

    //Add Showtime with Non-Existing Movie
    @Test
    void addShowtime_NonExistingMovie_Returns404() throws Exception {
        when(showtimeService.addShowtime(any(Showtime.class)))
                .thenThrow(new ResourceNotFoundException("Movie ID 99 does not exist."));

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "movieId": 99,
                    "theater": "Main Theater",
                    "startTime": "2025-04-01T14:00:00",
                    "endTime": "2025-04-01T16:00:00",
                    "price": 20.0
                }
                """))
                .andExpect(status().isNotFound());
    }

    //Successfully Get Showtime by ID
    @Test
    void getShowtimeById_Success_Returns200() throws Exception {
        when(showtimeService.getShowtimeById(1L)).thenReturn(showtime);

        mockMvc.perform(get("/showtimes/1"))
                .andExpect(status().isOk());
    }

    // Get Non-Existing Showtime by ID
    @Test
    void getShowtimeById_NonExisting_Returns404() throws Exception {
        when(showtimeService.getShowtimeById(1L))
                .thenThrow(new ResourceNotFoundException("Showtime not found with ID: 1"));

        mockMvc.perform(get("/showtimes/1"))
                .andExpect(status().isNotFound());
    }

    //Successfully Delete Showtime
    @Test
    void deleteShowtime_Success_Returns200() throws Exception {
        doNothing().when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/1"))
                .andExpect(status().isOk());
    }

    // Delete Non-Existing Showtime
    @Test
    void deleteShowtime_NonExisting_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Showtime not found with ID: 1"))
                .when(showtimeService).deleteShowtime(1L);

        mockMvc.perform(delete("/showtimes/1"))
                .andExpect(status().isNotFound());
    }

    //Invalid ID Format for Showtime
    @Test
    void deleteShowtime_InvalidIdFormat_Returns400() throws Exception {
        mockMvc.perform(delete("/showtimes/invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
