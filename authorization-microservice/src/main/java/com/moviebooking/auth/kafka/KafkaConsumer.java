package com.moviebooking.auth.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    //fix we will use logger here if requirement come
	 private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message){
    	LOGGER.info(String.format("Message recieved in authorization ms--- -> %s", message));
        System.out.println(String.format("Message received from movie-> %s", message));
    }
}