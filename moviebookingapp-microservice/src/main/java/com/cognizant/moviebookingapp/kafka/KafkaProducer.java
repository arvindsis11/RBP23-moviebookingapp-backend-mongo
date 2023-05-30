package com.cognizant.moviebookingapp.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    //constructor injection here--instead of autowired for testing--fix-priority low
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message){
    	LOGGER.info(String.format("Message sent from moviebook app--- -> %s", message));
        System.out.println(String.format("kafka Message sent from movie: %s", message));
        kafkaTemplate.send(topicName, message);
    }
}