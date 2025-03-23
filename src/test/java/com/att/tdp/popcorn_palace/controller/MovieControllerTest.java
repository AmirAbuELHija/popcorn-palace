package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.att.tdp.popcorn_palace.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MovieControllerTest {

    @InjectMocks
    private MovieController movieController;

    @Mock
    private MovieService movieService;

    private MockMvc mockMvc;

    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
        movie = new Movie(1L, "Inception", "Sci-Fi", 148, 8.8, 2010);
    }

    //(Returns Empty Array)
    @Test
    void getAllMovies_EmptyList_ReturnsEmptyArray() throws Exception {
        when(movieService.getAllMovies()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // ✅ Empty JSON array
    }

    //get all movies for large dataset
    @Test
    void getAllMovies_LargeDataset_ReturnsAllMovies() throws Exception {
        List<Movie> largeList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            largeList.add(new Movie((long) i, "Movie " + i, "Genre", 120, 7.5, 2022));
        }

        when(movieService.getAllMovies()).thenReturn(largeList);

        mockMvc.perform(get("/movies/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1000)); // ✅ Correctly verifies 1000 movies
    }

    // Adding a Movie with Minimum Values
    @Test
    void addMovie_MinimumValues_Success() throws Exception {
        Movie minimalMovie = new Movie(2L, "amir", "A", 1, 1.0, 1900);
        when(movieService.addMovie(any(Movie.class))).thenReturn(minimalMovie);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "amir",
                    "genre": "A",
                    "duration": 1,
                    "rating": 1.0,
                    "releaseYear": 1900
                }
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("amir"));
    }

    //Adding a Movie with Maximum Values
    @Test
    void addMovie_MaximumValues_Success() throws Exception {
        Movie maximalMovie = new Movie(3L, "2012", "Adventure", 300, 10.0, 2100);
        when(movieService.addMovie(any(Movie.class))).thenReturn(maximalMovie);
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "2012",
                    "genre": "Adventure",
                    "duration": 300,
                    "rating": 10.0,
                    "releaseYear": 2020
                }
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("2012"));
    }

    // adding an empty fields
    @Test
    void addMovie_EmptyFields_Returns400() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "",
                    "genre": "",
                    "duration": -1,
                    "rating": 20.0,
                    "releaseYear": 3000
                }
                """))
                .andExpect(status().isBadRequest());
    }

    //Updating a Non-Existing Movie (Error)
    @Test
    void updateMovie_NonExistingMovie_Returns404() throws Exception {
        when(movieService.updateMovie(anyString(), any(Movie.class)))
                .thenThrow(new ResourceNotFoundException("Movie not found with title: NonExisting"));

        mockMvc.perform(post("/movies/update/NonExisting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "title": "New Title",
                    "genre": "Drama",
                    "duration": 100,
                    "rating": 8.0,
                    "releaseYear": 2022
                }
                """))
                .andExpect(status().isNotFound());
    }

    // delete a movie
    @Test
    void deleteMovie_WithSpaces_Success() throws Exception {
        mockMvc.perform(delete("/movies/Inception"))
                .andExpect(status().isOk());
    }
}
