package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    //Get All Movies
    public List<Movie> getAllMovies() {
        List<Movie> movie = movieRepository.findAll();
        return movie;
    }

    // Add New Movie
    @Transactional
    public Movie addMovie(Movie movie) {
        //check if the movie already exist
        if (movieRepository.findByTitle(movie.getTitle()).isPresent()) {
            throw new DataConflictException("Movie with title '" + movie.getTitle() + "' already exists.");
        }
        return movieRepository.save(movie);
    }

    //Update Movie by Title
    @Transactional
    public Movie updateMovie(String title, Movie updatedMovie) {
        Movie existingMovie = movieRepository.findByTitle(title)
                //check if the movie_title is in the database
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with title: " + title));

        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setDuration(updatedMovie.getDuration());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setReleaseYear(updatedMovie.getReleaseYear());

        return movieRepository.save(existingMovie);
    }

    //Delete Movie by Title
    @Transactional
    public void deleteMovie(String title) {
        Movie existingMovie = movieRepository.findByTitle(title)
                //check if the movie_title is in the database
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with title: " + title));

        movieRepository.delete(existingMovie);
    }
}
