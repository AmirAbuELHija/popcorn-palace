package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.exception.*;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    //Add a new showtime
    @Transactional
    public Showtime addShowtime(Showtime showtime) {

        Movie movie = movieRepository.findById(showtime.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie ID " + showtime.getMovieId() + " does not exist."));


        long showtimeDurationMinutes = Duration.between(showtime.getStartTime(), showtime.getEndTime()).toMinutes();
        //check the time range and duration
        if (showtimeDurationMinutes < movie.getDuration()) {
            throw new InvalidInputException("Showtime duration cannot be shorter than the movie duration.");
        }
        //Duration too long (more than 10 mins over movie duration)
        if (showtimeDurationMinutes > movie.getDuration() + 10 ) {
            throw new InvalidInputException("Showtime duration cannot be more than 10 minutes longer than the movie duration.");
        }

        boolean overlappingExists = showtimeRepository.existsByTheaterAndOverlappingTimeRange(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime());

        if (overlappingExists) {
            throw new DataConflictException("Showtime conflicts with an existing showtime in the same theater.");
        }

        return showtimeRepository.save(showtime);
    }



    //Update an existing showtime
    @Transactional
    public Showtime updateShowtime(Long id, Showtime updatedShowtime) {
        Showtime existingShowtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with ID: " + id));

        Movie movie = movieRepository.findById(updatedShowtime.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie ID " + updatedShowtime.getMovieId() + " does not exist."));

        long showtimeDurationMinutes = Duration.between(updatedShowtime.getStartTime(), updatedShowtime.getEndTime()).toMinutes();
        long movieDurationMinutes = movie.getDuration();
        //problem with error throwing
        if (showtimeDurationMinutes < (movieDurationMinutes - 5) ||
                showtimeDurationMinutes > (movieDurationMinutes + 10)) {
            throw new InvalidInputException("Showtime duration must be within ±5 minutes of the movie's duration.");
        }

        // Improved Conflict Check for Updates
        boolean overlappingExists = showtimeRepository.existsByTheaterAndOverlappingTimeRangeExcludingSelf(
                updatedShowtime.getTheater(),
                updatedShowtime.getStartTime().minusMinutes(5), // 5-minute flexibility
                updatedShowtime.getEndTime().plusMinutes(5),     // 5-minute flexibility
                existingShowtime.getId()                         // Exclude itself
        );

        if (overlappingExists) {
            throw new DataConflictException("Showtime conflicts with an existing showtime in the same theater.");
        }

        // Update Showtime Details
        existingShowtime.setTheater(updatedShowtime.getTheater());
        existingShowtime.setStartTime(updatedShowtime.getStartTime());
        existingShowtime.setEndTime(updatedShowtime.getEndTime());
        existingShowtime.setPrice(updatedShowtime.getPrice());

        return showtimeRepository.save(existingShowtime);
    }



    // Get a showtime by ID
    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findById(id)
                //check if the showtime id exists
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found with ID: " + id));
    }

    //get all showtimes (for testing)
    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = showtimeRepository.findAll();
        return showtimes;//return empty list if there is nothing
    }

    // Delete a showtime by ID
    @Transactional
    public void deleteShowtime(Long id) {
        //check if the showtime id exists
        if (!showtimeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showtime not found with ID: " + id);
        }

        showtimeRepository.deleteById(id);
    }
}
