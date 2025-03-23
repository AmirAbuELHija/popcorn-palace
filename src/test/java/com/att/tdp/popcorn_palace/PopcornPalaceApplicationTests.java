package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.controller.MovieController;
import com.att.tdp.popcorn_palace.controller.ShowtimeController;
import com.att.tdp.popcorn_palace.controller.BookingController;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PopcornPalaceApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Autowired
	private MovieController movieController;

	@Autowired
	private ShowtimeController showtimeController;

	@Autowired
	private BookingController bookingController;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ShowtimeRepository showtimeRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private DataSource dataSource;  // For database connectivity test

	//Context Load Test (Core test) to check everything is init correctly
	@Test
	void contextLoads() {
		assertThat(context).isNotNull();
		assertThat(showtimeRepository).isNotNull();
		assertThat(movieController).isNotNull();
		assertThat(showtimeController).isNotNull();
		assertThat(bookingController).isNotNull();
		assertThat(movieRepository).isNotNull();
		assertThat(bookingRepository).isNotNull();
	}

	//basic Database Connectivity Test
	@Test
	void databaseConnectivityTest() throws Exception {
		assertThat(dataSource).isNotNull();
		assertThat(dataSource.getConnection()).isNotNull();
	}

	// Basic functionality check for endpoint respond
	@Test
	void getAllMovies_EndpointWorks() {
		String baseUrl = "http://localhost:" + port + "/movies/all";
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void getAllShowtimes_EndpointWorks() {
		String baseUrl = "http://localhost:" + port + "/showtimes/all";
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}


}
