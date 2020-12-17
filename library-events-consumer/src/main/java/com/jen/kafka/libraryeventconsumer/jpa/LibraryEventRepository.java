package com.jen.kafka.libraryeventconsumer.jpa;

import org.springframework.data.repository.CrudRepository;

import com.jen.kafka.libraryeventconsumer.entity.LibraryEvent;

public interface LibraryEventRepository extends CrudRepository<LibraryEvent,String>{

}
