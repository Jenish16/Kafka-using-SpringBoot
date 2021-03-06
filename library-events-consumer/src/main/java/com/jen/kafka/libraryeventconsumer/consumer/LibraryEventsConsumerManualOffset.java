package com.jen.kafka.libraryeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class LibraryEventsConsumerManualOffset implements AcknowledgingMessageListener<String,String> {


    @Override
    @KafkaListener(topics = {"h4dbfpf4-temp"})
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ConsumerRecord : {} ", consumerRecord );
        
        //To Manually acknowledge Offset value to Kafka cluster
        //we need to set enable.auto.commit as false , so it'll not commit offset automatically
        acknowledgment.acknowledge();
    }
}