package com.jen.kafka.libraryeventconsumer.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.jen.kafka.libraryeventconsumer.service.LibraryEventsService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableKafka
@Slf4j
public class LibraryEventsConsumerConfig {
	
	@Autowired
	LibraryEventsService libraryEventsService;


	@Bean ConcurrentKafkaListenerContainerFactory<?, ?>
	kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object,Object> kafkaConsumerFactory) {

		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new
				ConcurrentKafkaListenerContainerFactory<>(); 
		configurer.configure(factory,kafkaConsumerFactory);

		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		factory.setErrorHandler(((thrownException, data) -> {
			log.info("Exception in consumerConfig is {} and the record is {}", thrownException.getMessage(), data);
			
			//persist logic for failed record
		}));

		//Configuration for Retry if Exception
		factory.setRetryTemplate(retryTemplate());
		
		//Configuration for Recovery if Retry is exhausted
		  factory.setRecoveryCallback((context -> {
	            if(context.getLastThrowable().getCause() instanceof RecoverableDataAccessException){
	                //invoke recovery logic
	                log.info("Inside the recoverable logic");
	                Arrays.asList(context.attributeNames())
	                        .forEach(attributeName -> {
	                    log.info("Attribute name is : {} ", attributeName);
	                    log.info("Attribute Value is : {} ", context.getAttribute(attributeName));
	                });

	                
	                ConsumerRecord<String, String> consumerRecord = (ConsumerRecord<String, String>) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
	                //to handle recovery(Here publishing same record to topic)
	                libraryEventsService.handleRecovery(consumerRecord);
	            }else{
	                log.info("Inside the non recoverable logic");
	                throw new RuntimeException(context.getLastThrowable().getMessage());
	            }
	            
	            //To Manually acknowledge to kafka cluster
	            Acknowledgment ack = (Acknowledgment) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_ACKNOWLEDGMENT);
	            ack.acknowledge();
	            return null;
	        }));
		
		return factory;
	}

	private RetryTemplate retryTemplate() {

		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		
		//Back of period between retry 
		fixedBackOffPolicy.setBackOffPeriod(2000);
		
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(simpleRetryPolicy());
		retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
		return  retryTemplate;
	}

	private RetryPolicy simpleRetryPolicy() {

		/*SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
		 	configure how many times want to retry
	        simpleRetryPolicy.setMaxAttempts(3);*/
		Map<Class<? extends Throwable>, Boolean> exceptionsMap = new HashMap<>();
		exceptionsMap.put(IllegalArgumentException.class, false);
		exceptionsMap.put(RecoverableDataAccessException.class, true);
		
		//configure how many times want to retry and for which exceptions
		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3,exceptionsMap,true);
		return simpleRetryPolicy;
	}

}

