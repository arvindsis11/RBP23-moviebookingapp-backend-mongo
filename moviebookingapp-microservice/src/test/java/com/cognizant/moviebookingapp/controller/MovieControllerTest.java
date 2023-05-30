package com.cognizant.moviebookingapp.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.cognizant.moviebookingapp.model.Movie;
import com.cognizant.moviebookingapp.model.Ticket;
import com.cognizant.moviebookingapp.service.MovieService;
import com.cognizant.moviebookingapp.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	MovieService movieService;

	@MockBean
	TicketService ticketService;

	@MockBean
	AuthService authService;

//	@InjectMocks
//	MovieController movieController;

	@Test
	public void testAddMovies_Success() throws Exception {
		// Mock authentication
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("admin", "ROLE_ADMIN");
		when(authService.validateToken(anyString())).thenReturn(userInfo);
		Movie movie = new Movie();
		movie.setMovieName("endgame11");
		movie.setTheaterName("pvr");
		when(movieService.addMovie(movie)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

		// Perform the request
		mockMvc.perform(post("/api/v1.0/moviebooking/addmovie").header("Authorization", "testtoken")
				.contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(movie)))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetAllMovies_Success() throws Exception {
		// Mock authentication
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("admin", "ROLE_ADMIN");
		when(authService.validateToken(anyString())).thenReturn(userInfo);

		HashSet<String> set = new HashSet<>(Arrays.asList("A1", "A2"));
		List<Movie> movies = Arrays.asList(
				new Movie("test", "test", "test", 100, "test", new HashSet<>(), new ArrayList<>()),
				new Movie("test1", "test", "test", 100, "test", new HashSet<>(), new ArrayList<>()));
		ResponseEntity<List<Movie>> expectedResponse = new ResponseEntity<>(movies, HttpStatus.OK);
		System.err.println(expectedResponse.getBody());
		when(movieService.getAllMovies()).thenReturn(expectedResponse);

		// Perform the request
		mockMvc.perform(get("/api/v1.0/moviebooking/getAllMovies").header("Authorization", "validToken"))
				.andExpect(status().isOk()).andExpect(content().json(new ObjectMapper().writeValueAsString(movies)));
		;
	}

	@Test
	void testBookMovie() throws  Exception {
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("admin", "ROLE_ADMIN");
		when(authService.validateToken(anyString())).thenReturn(userInfo);

		// Create a ticket object for the request body
		Ticket ticket = new Ticket();
		ticket.setUserId("admin");

		// Perform the request
		mockMvc.perform(post("/api/v1.0/moviebooking/book/{movieName}", "movie1").header("Authorization", "valid-token")
				.contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(ticket)))
				.andExpect(status().isOk());

	}

}
