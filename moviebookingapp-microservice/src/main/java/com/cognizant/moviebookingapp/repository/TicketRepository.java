package com.cognizant.moviebookingapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.cognizant.moviebookingapp.model.Ticket;

@EnableMongoRepositories
public interface TicketRepository extends MongoRepository<Ticket, String>{
	
	List<Ticket> findByUserId(String userId);

}
