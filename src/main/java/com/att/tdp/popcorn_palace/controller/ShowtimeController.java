package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // Get Showtime all
    @GetMapping("/all")
    public ResponseEntity<List<Showtime>> findeAll() {
        List<Showtime> founded=  showtimeService.getAllShowtimes();
        return ResponseEntity.ok(founded);
    }

    // Get Showtime by id
    @GetMapping("/{showtimeId}")
    public ResponseEntity<Showtime> findById(@PathVariable Long showtimeId) {
        Showtime founded=showtimeService.getShowtimeById(showtimeId);
        return ResponseEntity.ok(founded);
    }

    // Add New Showtime
    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@Valid @RequestBody Showtime showtime) {
        return ResponseEntity.ok(showtimeService.addShowtime(showtime));
    }

    // Update Showtime by ID
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long showtimeId, @Valid @RequestBody Showtime updatedShowtime) {
        showtimeService.updateShowtime(showtimeId, updatedShowtime);
        return ResponseEntity.ok().build();
    }

    // Delete Showtime by ID
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }
}
