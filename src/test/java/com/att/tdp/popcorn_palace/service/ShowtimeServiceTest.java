package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShowtimeServiceTest {

    @InjectMocks
    private ShowtimeService showtimeService;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    private Showtime showtime;
    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        movie = new Movie(2L, "Inception", "Sci-Fi", 148, 8.8, 2010);

        showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovieId(2L);
        showtime.setTheater("Sample Theater");
        showtime.setStartTime(LocalDateTime.now().plusDays(1));
        showtime.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(148));
        showtime.setPrice(25.0);
    }

    //Successfully Create a Showtime
    @Test
    void addShowtime_SuccessfulCreation_ReturnsShowtime() {
        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.existsByTheaterAndOverlappingTimeRange(anyString(), any(), any()))
                .thenReturn(false);
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        Showtime result = showtimeService.addShowtime(showtime);

        assertNotNull(result);
        assertEquals(showtime.getId(), result.getId());
    }

    //Showtime Exactly 10 Minutes Longer Than Movie Duration
    @Test
    void addShowtime_Exactly10MinutesOver_Success() {
        showtime.setEndTime(showtime.getStartTime().plusMinutes(movie.getDuration() + 10));

        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(showtime);

        Showtime result = showtimeService.addShowtime(showtime);

        assertNotNull(result);
    }

    //Showtime Longer Than 10 Minutes Over Movie Duration
    @Test
    void addShowtime_TooLongDuration_ThrowsInvalidInputException() {
        showtime.setEndTime(showtime.getStartTime().plusMinutes(movie.getDuration() + 11));

        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));

        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> showtimeService.addShowtime(showtime)
        );

        assertEquals("Showtime duration cannot be more than 10 minutes longer than the movie duration.", exception.getMessage());
    }

    //Showtime Shorter Than Movie Duration
    @Test
    void addShowtime_ShorterThanMovieDuration_ThrowsInvalidInputException() {
        showtime.setEndTime(showtime.getStartTime().plusMinutes(movie.getDuration() - 5));

        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));

        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> showtimeService.addShowtime(showtime)
        );

        assertEquals("Showtime duration cannot be shorter than the movie duration.", exception.getMessage());
    }

    //Successfully Update a Showtime
    @Test
    void updateShowtime_ExistingShowtime_UpdatesSuccessfully() {
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setId(1L);
        updatedShowtime.setMovieId(2L);
        updatedShowtime.setTheater("Updated Theater");
        updatedShowtime.setStartTime(LocalDateTime.now().plusDays(2));
        updatedShowtime.setEndTime(updatedShowtime.getStartTime().plusMinutes(movie.getDuration()));
        updatedShowtime.setPrice(30.0);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
        when(movieRepository.findById(2L)).thenReturn(Optional.of(movie));
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(updatedShowtime);

        Showtime result = showtimeService.updateShowtime(1L, updatedShowtime);

        assertNotNull(result);
        assertEquals("Updated Theater", result.getTheater());
        assertEquals(30.0, result.getPrice());
    }

    //Update Non-Existing Showtime
    @Test
    void updateShowtime_NonExistingShowtime_ThrowsResourceNotFoundException() {
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setId(2L);

        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> showtimeService.updateShowtime(1L, updatedShowtime)
        );

        assertEquals("Showtime not found with ID: 1", exception.getMessage());
    }

    //Successfully Delete Showtime
    @Test
    void deleteShowtime_Success_Returns200() {
        when(showtimeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> showtimeService.deleteShowtime(1L));
        verify(showtimeRepository, times(1)).deleteById(1L);
    }

    //Delete Non-Existing Showtime
    @Test
    void deleteShowtime_NonExistingShowtime_ThrowsResourceNotFoundException() {
        when(showtimeRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> showtimeService.deleteShowtime(1L)
        );

        assertEquals("Showtime not found with ID: 1", exception.getMessage());
    }

    //Successfully Get a Showtime by ID
    @Test
    void getShowtimeById_Success_ReturnsShowtime() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

        Showtime result = showtimeService.getShowtimeById(1L);

        assertNotNull(result);
        assertEquals(showtime.getId(), result.getId());
    }

    //Get Non-Existing Showtime by ID
    @Test
    void getShowtimeById_NonExistingShowtime_ThrowsResourceNotFoundException() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> showtimeService.getShowtimeById(1L)
        );
        assertEquals("Showtime not found with ID: 1", exception.getMessage());
    }
}
