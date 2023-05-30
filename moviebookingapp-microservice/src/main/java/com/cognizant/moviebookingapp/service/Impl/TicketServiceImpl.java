package com.cognizant.moviebookingapp.service.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cognizant.moviebookingapp.model.Movie;
import com.cognizant.moviebookingapp.model.Ticket;
import com.cognizant.moviebookingapp.repository.MovieRepository;
import com.cognizant.moviebookingapp.repository.TicketRepository;
import com.cognizant.moviebookingapp.service.TicketService;

/**
 * @author arvind 
 * todo--fix: add exception handling here: scope:--> invalid inputs
 */
@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	TicketRepository ticketRepo;

	@Autowired
	MovieRepository movieRepo;

	@Override
	public ResponseEntity<?> bookMovie(String movieName, Ticket ticket) {
		Optional<Movie> movie = movieRepo.findByMovieName(movieName);
		if (movie.isEmpty()) {
			return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
		}

		int availableTickets = movie.get().getTotalTickets();// from movie document
		int totalTicketsBuy = ticket.getNumberOfTickets();// from ticket object
		if (availableTickets <= 0) {
			return new ResponseEntity<>("All tickets sold out", HttpStatus.BAD_REQUEST);
		}
		if (availableTickets < totalTicketsBuy) {
			return new ResponseEntity<>("Insufficient tickets available", HttpStatus.BAD_REQUEST);
		}

		// Checking into movie DB if seat is booked or not
		Set<String> bookedSeatsMovie = movie.get().getBookedSeats();// this is type of set<String> in movie model
		Set<String> seatNumbersTicket = ticket.getSeatNumbers();// this is typep of set<String> in ticket model

		// Check if user enters more tickets than  seats,A1,A2.. in the movie
		if (totalTicketsBuy != seatNumbersTicket.size()) {
			return new ResponseEntity<>("Number of tickets does not match number of seat numbers",
					HttpStatus.BAD_REQUEST);
		}

		// Check that each seat number is valid and less than or equal to total seats in
		// the movie --can be removed if validation done in frontend--planned
		Set<String> invalidSeatNumbers = validateSeatNumbers(ticket.getSeatNumbers());
		if (!invalidSeatNumbers.isEmpty()) {
			return new ResponseEntity<>("Invalid seat numbers: " + invalidSeatNumbers, HttpStatus.BAD_REQUEST);
		}

		// Check for already booked seat numbers--can be removed if validation done in
		// frontend--will remove this 
		Set<String> duplicates = findDuplicates(bookedSeatsMovie, seatNumbersTicket);
		if (!duplicates.isEmpty()) {
			return new ResponseEntity<>("Seats are already booked: " + duplicates, HttpStatus.CONFLICT);
		}

		// Update final ticket count and movie data
		int finalTicketCount = availableTickets - totalTicketsBuy;
		Ticket newTicket = new Ticket();
		newTicket.setNumberOfTickets(ticket.getNumberOfTickets());
		newTicket.setSeatNumbers(ticket.getSeatNumbers());
		newTicket.setTransactionId(generateTransactionId());
		newTicket.setMovieName(movieName);
		newTicket.setTheaterName(movie.get().getTheaterName());
		newTicket.setUserId(ticket.getUserId());// --fix-me planned

		bookedSeatsMovie.addAll(seatNumbersTicket);
		movie.get().getTickets().add(newTicket);
		movie.get().setTotalTickets(finalTicketCount);// updating final count of ticket in movie db
		movieRepo.save(movie.get());
		ticketRepo.save(newTicket);
		//testing purpose only
		Map<String,String> mapObj = new HashMap<>();
		mapObj.put("booked movie ", movieName);
		return new ResponseEntity<>(mapObj, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> getAllTickets() {
		try {
			List<Ticket> tickets = ticketRepo.findAll();
			return new ResponseEntity<>(tickets, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occurred while fetching tickets: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getTicketsUser(String userId) {
		// todo: get ticket list for a specific user
		List<Ticket> userTickets = ticketRepo.findByUserId(userId);
		return new ResponseEntity<>(userTickets, HttpStatus.OK);
	}

	// for generating unique txn id
	public static String generateTransactionId() {
		final String PREFIX = "TXN-";
		final int RANDOM_BOUND = 100;
		Random random = new Random();
		int randomNumber = random.nextInt(RANDOM_BOUND);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String timestamp = now.format(formatter);
		String transactionId = PREFIX + timestamp + randomNumber;
		return transactionId;
	}

	// finding if seat is already there in movie document--intersection retailAll
	// only works with integer types
	private Set<String> findDuplicates(Set<String> set1, Set<String> set2) {
		Set<String> duplicates = new HashSet<>();
		for (String s : set1) {
			if (set2.contains(s)) {
				duplicates.add(s);
			}
		}
		return duplicates;
	}
//will remove this
	private Set<String> validateSeatNumbers(Set<String> seatNumbersTicket) {
		Set<String> invalidSeatNumbers = new HashSet<>();
		for (String seatNumber : seatNumbersTicket) {
			if (!seatNumber.matches("^[A-J]\\d{1,2}$")) { // Check format of seat number (e.g. A1, B2, etc.)
				invalidSeatNumbers.add(seatNumber);
			} else {
				int row = seatNumber.charAt(0) - 'A' + 1; // Convert row letter to number (A=1, B=2, etc.)
				int col = Integer.parseInt(seatNumber.substring(1)); // Get column number
				if (row < 1 || row > 10 || col < 1 || col > 10) {
					invalidSeatNumbers.add(seatNumber);// if user enters eg. A12
				}
			}
		}
		return invalidSeatNumbers;
	}

}
