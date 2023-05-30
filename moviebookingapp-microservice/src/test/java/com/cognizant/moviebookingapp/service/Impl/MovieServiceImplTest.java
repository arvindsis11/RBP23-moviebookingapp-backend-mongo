package com.cognizant.moviebookingapp.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cognizant.moviebookingapp.model.Movie;
import com.cognizant.moviebookingapp.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

	@Mock
	MovieRepository movieRepo;

	@InjectMocks
	MovieServiceImpl movieService;

//	@BeforeEach
//	void setup() {
//	    Movie movie = new Movie();
//	    movie.setMovieName("endgame");
//	    movie.setTotalTickets(100);
//Mockito.any(Movie.class)--fix
//	    when(movieRepo.findByMovieName("endgame")).thenReturn(Optional.of(movie));
//	}

	@Test
	void testAddMovieSaved() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.existsByMovieName("endgame")).thenReturn(false);
		when(movieRepo.save(movie)).thenReturn(movie);
		ResponseEntity<?> res = movieService.addMovie(movie);
		assertEquals(movie, res.getBody());
	}

	@Test
	void testAddMovie_notSaved() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.existsByMovieName("endgame")).thenReturn(true);
		ResponseEntity<?> res = movieService.addMovie(movie);
		assertEquals("movie already exists", res.getBody());
	}

	@Test
	void testGetAllMovies() {
		List<Movie> moviesList = Stream.of(new Movie(), new Movie()).collect(Collectors.toList());
		when(movieRepo.findAll()).thenReturn(moviesList);
		ResponseEntity<List<Movie>> response = movieService.getAllMovies();
		assertEquals(moviesList, response.getBody());
	}

	@Test
	void testSearchMovieById() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.findByMovieId(anyString())).thenReturn(Optional.of(movie));
		ResponseEntity<?> response = movieService.searchMovieById(anyString());
		assertEquals(movie, response.getBody());
	}

	@Test
	void testDeleteMovie() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.findByMovieId(anyString())).thenReturn(Optional.of(movie));
		ResponseEntity<?> response = movieService.deleteMovie(anyString());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("movie deleted successfully!", response.getBody());
	}

	@Test
	void testUpdateMovie() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.findByMovieName("endgame")).thenReturn(Optional.of(movie));
		when(movieRepo.save(movie)).thenReturn(null);
		ResponseEntity<?> response = movieService.updateMovie("endgame", 10);
		assertEquals("ticket status updated: " + 10, response.getBody());
	}

	@Test
	void testGetBookedTicketList() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		when(movieRepo.findByMovieName(anyString())).thenReturn(Optional.of(movie));
		ResponseEntity<?> response = movieService.getBookedTicketList("endgame");
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
