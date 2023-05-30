package com.cognizant.moviebookingapp.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.cognizant.moviebookingapp.model.Ticket;
import com.cognizant.moviebookingapp.repository.MovieRepository;
import com.cognizant.moviebookingapp.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

	@Mock
	TicketRepository ticketRepo;

	@Mock
	MovieRepository movieRepo;

	@InjectMocks
	TicketServiceImpl ticketService;

	@Test
	public void testBookMovie_Success() {
		Movie movie = new Movie();
		movie.setMovieName("endgame");
		movie.setTotalTickets(100);
		movie.setBookedSeats(new HashSet<>());
		Ticket ticket = new Ticket();
		ticket.setNumberOfTickets(2);
		ticket.setSeatNumbers(new HashSet<>(Arrays.asList("A1", "A2")));
		when(movieRepo.findByMovieName("endgame")).thenReturn(Optional.of(movie));
		when(ticketRepo.save(any(Ticket.class))).thenReturn(ticket);

		ResponseEntity<?> response = ticketService.bookMovie("endgame", ticket);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void testGetAllTickets() {
		Ticket ticket = new Ticket();
		ticket.setNumberOfTickets(2);
		ticket.setSeatNumbers(new HashSet<>(Arrays.asList("A1", "A2")));
		List<Ticket> ticketList = Stream.of(ticket).collect(Collectors.toList());
		when(ticketRepo.findAll()).thenReturn(ticketList);
		ResponseEntity<?> res = ticketService.getAllTickets();
		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertEquals(ticketList, res.getBody());
	}

	@Test
	void testGetTicketsUser() {
		String userId = "user123";
		Set<String> seats = new HashSet<>(Arrays.asList("A1", "A2"));
		List<Ticket> userTickets = Arrays.asList(new Ticket("TXN-test1", 2, seats, "endgame", "theater", userId),
				new Ticket("TXN-test2", 2, seats, "endgame", "theater", userId));
		when(ticketRepo.findByUserId(userId)).thenReturn(userTickets);
		ResponseEntity<?> response = ticketService.getTicketsUser(userId);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(userTickets, response.getBody());
	}

}
