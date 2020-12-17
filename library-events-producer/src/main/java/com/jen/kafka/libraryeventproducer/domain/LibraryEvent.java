package com.jen.kafka.libraryeventproducer.domain;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {

	private String libraryEventId;

	private LibraryEventType libraryEventType;

	@NotNull
	@Valid
	private Book book;

}
