package org.example.expert.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private TodoRepository todoRepository;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	void 댓글_저장에_성공한다() {

		// given
		User user = new User("test@naver.com", "1234", "test", UserRole.USER);
		AuthUser authUser = new AuthUser(user);
		User fromAuthUser = User.fromAuthUser(authUser);

		Long todoId = 1L;
		Todo todo = new Todo("제목", "내용", "날씨", fromAuthUser);

		CommentSaveRequest commentSaveRequest = new CommentSaveRequest("댓글");

		Comment comment = new Comment(commentSaveRequest.getContents(), fromAuthUser, todo);

		when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);

		// when
		CommentSaveResponse commentSaveResponse = commentService.saveComment(authUser, todoId, commentSaveRequest);

		// then
		assertThat(commentSaveResponse.getContents()).isEqualTo("댓글");
		assertThat(commentSaveResponse.getUser().getEmail()).isEqualTo("test@naver.com");
	}

	@Test
	void 댓글_조회() {

		// given
		//commentService.getComments(todoId)

		User user = new User("test@naver.com", "1234", "test", UserRole.USER);
		AuthUser authUser = new AuthUser(user);
		User fromAuthUser = User.fromAuthUser(authUser);

		Long todoId = 1L;
		Todo todo = new Todo("제목", "내용", "날씨", fromAuthUser);

		Comment comment = new Comment("댓글1", fromAuthUser, todo);

		User user1 = new User("abs@naver.com", "1234", "abs", UserRole.USER);
		AuthUser authUser1 = new AuthUser(user1);
		User fromAuthUser1 = User.fromAuthUser(authUser1);
		Comment comment1 = new Comment("댓글2", fromAuthUser1, todo);

		List<Comment> comments = List.of(comment, comment1);

		when(commentRepository.findByTodoIdWithUser(todoId)).thenReturn(comments);

		// when
		List<CommentResponse> commentResponseList = commentService.getComments(todoId);

		// then
		assertThat(commentResponseList).hasSize(2);
		assertThat(commentResponseList.get(0).getContents()).isEqualTo("댓글1");
		assertThat(commentResponseList.get(0).getUser().getEmail()).isEqualTo("test@naver.com");
		assertThat(commentResponseList.get(1).getContents()).isEqualTo("댓글2");
		assertThat(commentResponseList.get(1).getUser().getEmail()).isEqualTo("abs@naver.com");

	}
}