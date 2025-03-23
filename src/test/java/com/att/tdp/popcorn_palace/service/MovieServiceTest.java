package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setRating(8.8);
        movie.setReleaseYear(2010);
    }

    //Successful Movie Creation
    @Test
    void addMovie_SuccessfulCreation_ReturnsMovie() {
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        Movie result = movieService.addMovie(movie);
        assertNotNull(result);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    //Adding a Movie That Already Exists
    @Test
    void addMovie_AlreadyExists_ThrowsDataConflictException() {
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.of(movie));
        DataConflictException exception = assertThrows(DataConflictException.class, () -> movieService.addMovie(movie));
        assertEquals("Movie with title 'Inception' already exists.", exception.getMessage());
    }

    //Successfully Updating an Existing Movie
    @Test
    void updateMovie_ExistingMovie_UpdatesSuccessfully() {
        Movie updatedMovie = new Movie();
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(120);
        updatedMovie.setRating(9.0);
        updatedMovie.setReleaseYear(2024);
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
        Movie result = movieService.updateMovie(movie.getTitle(), updatedMovie);
        assertNotNull(result);
        assertEquals("Action", result.getGenre());
        assertEquals(120, result.getDuration());
        assertEquals(9.0, result.getRating());
        assertEquals(2024, result.getReleaseYear());
    }

    //Updating a Non-Existing Movie
    @Test
    void updateMovie_NonExistingMovie_ThrowsResourceNotFoundException() {
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(movie.getTitle(), movie));
        assertEquals("Movie not found with title: Inception", exception.getMessage());
    }

    //Successfully Deleting an Existing Movie
    @Test
    void deleteMovie_ExistingMovie_DeletesSuccessfully() {
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.of(movie));
        assertDoesNotThrow(() -> movieService.deleteMovie(movie.getTitle()));
        verify(movieRepository, times(1)).delete(movie);
    }

    //Deleting a Non-Existing Movie
    @Test
    void deleteMovie_NonExistingMovie_ThrowsResourceNotFoundException() {
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(movie.getTitle()));
        assertEquals("Movie not found with title: Inception", exception.getMessage());
    }

    //Fetching All Movies
    @Test
    void getAllMovies_ReturnsListOfMovies() {
        List<Movie> movieList = Arrays.asList(movie);
        when(movieRepository.findAll()).thenReturn(movieList);
        List<Movie> result = movieService.getAllMovies();
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    //Empty Movie List (404 Error)
    @Test
    void getAllMovies_EmptyList_ReturnsEmptyList() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<Movie> result = movieService.getAllMovies();

        assertTrue(result.isEmpty());
    }
}
