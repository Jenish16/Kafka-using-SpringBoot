package com.jen.kafka.libraryeventconsumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class LibraryEventsConsumerConfig {



//	@Bean ConcurrentKafkaListenerContainerFactory<?, ?>
//	kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//			ConsumerFactory<Object,Object> kafkaConsumerFactory) {
//
//		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new
//				ConcurrentKafkaListenerContainerFactory<>(); 
//		configurer.configure(factory,kafkaConsumerFactory);
//
//		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.
//				MANUAL);
//
//		return factory;
//	}
}

