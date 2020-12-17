package com.jen.kafka.libraryeventconsumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jen.kafka.libraryeventconsumer.entity.LibraryEvent;
import com.jen.kafka.libraryeventconsumer.jpa.LibraryEventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryEventsService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	KafkaTemplate<Integer,String> kafkaTemplate;

	@Autowired
	private LibraryEventRepository libraryEventsRepository;


	public void processLibraryEvent(ConsumerRecord<String,String> consumerRecord) throws JsonProcessingException {
		LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
		log.info("libraryEvent : {} ", libraryEvent);

		if(libraryEvent.getLibraryEventId()!=null && libraryEvent.getLibraryEventId()==""){
			throw new RecoverableDataAccessException("Temporary Network Issue");
		}

		switch(libraryEvent.getLibraryEventType()){
		case NEW:
			save(libraryEvent);
			break;
		case UPDATE:
			
			save(libraryEvent);
			break;
		default:
			log.info("Invalid Library Event Type");
		}

	}
	private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the libary Event {} ", libraryEvent);
    }
}
