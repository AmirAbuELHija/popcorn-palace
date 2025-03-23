package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.Showtime;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("""
        SELECT COUNT(s) > 0
        FROM Showtime s
        WHERE s.theater = :theater
        AND (
            (:startTime BETWEEN s.startTime AND s.endTime)
            OR
            (:endTime BETWEEN s.startTime AND s.endTime)
            OR
            (s.startTime BETWEEN :startTime AND :endTime)
        )
    """)
    boolean existsByTheaterAndOverlappingTimeRange(@Param("theater") String theater, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Showtime s
        WHERE s.theater = :theater
        AND (
            (s.startTime < :endTime AND s.endTime > :startTime)
        )
        AND s.id <> :showtimeId
    """)
    boolean existsByTheaterAndOverlappingTimeRangeExcludingSelf(
            @Param("theater") String theater,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("showtimeId") Long showtimeId
    );}
