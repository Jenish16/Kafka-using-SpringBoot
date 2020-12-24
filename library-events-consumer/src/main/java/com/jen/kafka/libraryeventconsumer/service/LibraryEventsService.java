package com.jen.kafka.libraryeventconsumer.service;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
	KafkaTemplate<String,String> kafkaTemplate;

	@Autowired
	private LibraryEventRepository libraryEventsRepository;


	public void processLibraryEvent(ConsumerRecord<String,String> consumerRecord) throws JsonProcessingException {

		LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
		log.info("libraryEvent : {} ", libraryEvent);

		if(libraryEvent.getLibraryEventId()!=null && "000".equals(libraryEvent.getLibraryEventId())){
			throw new RecoverableDataAccessException("Temporary Network Issue");
		}

		switch(libraryEvent.getLibraryEventType()){
		case NEW:
			save(libraryEvent);
			break;
		case UPDATE:
			validate(libraryEvent);
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
	private void validate(LibraryEvent libraryEvent) {
		if(libraryEvent.getLibraryEventId()==null){
			throw new IllegalArgumentException("Library Event Id is missing");
		}

		Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
		if(!libraryEventOptional.isPresent()){
			throw new IllegalArgumentException("Not a valid library Event");
		}
		log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
	}


	public void handleRecovery(ConsumerRecord<String,String> record){

		log.info("inside handle recovery");
		String key = record.key();
		String message = record.value();

		ListenableFuture<SendResult<String,String>> listenableFuture = kafkaTemplate.sendDefault(key, message);
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, message, ex);
			}

			@Override
			public void onSuccess(SendResult<String, String> result) {
				handleSuccess(key, message, result);
			}
		});
	}

	private void handleFailure(String key, String value, Throwable ex) {
		log.error("Error Sending the Message and the exception is {}", ex.getMessage());
		try {
			throw ex;
		} catch (Throwable throwable) {
			log.error("Error in OnFailure: {}", throwable.getMessage());
		}
	}

	private void handleSuccess(String key, String value, SendResult<String, String> result) {
		log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
	}
}
