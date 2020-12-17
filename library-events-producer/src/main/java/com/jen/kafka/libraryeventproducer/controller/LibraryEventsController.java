package com.jen.kafka.libraryeventproducer.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jen.kafka.libraryeventproducer.domain.LibraryEvent;
import com.jen.kafka.libraryeventproducer.domain.LibraryEventType;
import com.jen.kafka.libraryeventproducer.producers.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LibraryEventsController {

	@Autowired
	LibraryEventProducer libraryEventProducer;


	@PostMapping("/libraryevent")
	public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException{
		log.info("Before Service Method Call");
		//This is asynchronous call
		//libraryEventProducer.sendLibraryEvent(libraryEvent);
		
		//This is Synchronous call
		//libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
		
		
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);		
		libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
		
		log.info("After Service Method Call");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}

	
	@PutMapping("/libraryevent")
	public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException{
		log.info("Before Service Method Call");
		if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }
		
		//This is asynchronous call
		//libraryEventProducer.sendLibraryEvent(libraryEvent);
		
		//This is Synchronous call
		//libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
		
		
		libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);		
		libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
		
		log.info("After Service Method Call");
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
}
