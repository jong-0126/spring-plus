package org.example.expert.domain.todo.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class TodoGetRequest {

	private final LocalDateTime startDate;

	private final LocalDateTime endDate;

	public TodoGetRequest(LocalDateTime startDate, LocalDateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
