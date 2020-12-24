package com.jen.kafka.libraryeventconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jen.kafka.libraryeventconsumer.service.LibraryEventsService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventsConsumer {

	@Value("${jen.kafka.topic-name}")
	String topic;
	
	@Autowired
    private LibraryEventsService libraryEventsService;

    @KafkaListener(topics = {"h4dbfpf4-temp2"})
    public void onMessage(ConsumerRecord<String,String> consumerRecord) throws JsonProcessingException {

    	
        log.info("ConsumerRecord : {} ", consumerRecord );
        libraryEventsService.processLibraryEvent(consumerRecord);

    }
}
