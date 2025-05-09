package org.example.expert.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class CommentServiceTest {

	@Mock
	private TodoRepository todoRepository;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	void saveComment() {

		// given

		// when

		// then
	}

	@Test
	void getComments() {

		// given
		// 유저 정보
		// 유저가 작성한 일정 정보


		// when

		// then
	}
}