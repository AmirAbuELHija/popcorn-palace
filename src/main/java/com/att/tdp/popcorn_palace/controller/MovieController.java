package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // Get All Movies
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    // Add a New Movie
    @PostMapping
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.addMovie(movie));
    }

    // Update Movie by Title
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String movieTitle, @Valid @RequestBody Movie updatedMovie) {
            movieService.updateMovie(movieTitle, updatedMovie);
            return ResponseEntity.ok().build();

    }

    // Delete Movie by Title
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@Valid @PathVariable String movieTitle) {
        movieService.deleteMovie(movieTitle);
        return ResponseEntity.ok().build();
    }
}
