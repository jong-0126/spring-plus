package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;

		Todo result = queryFactory
			.selectFrom(todo)
			.leftJoin(todo.user, user).fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public Page<TodoSearchResponse> findAllByTitleOrderByModifiedAtDesc(String title, LocalDateTime startDate, LocalDateTime endDate,
		String nickName, Pageable pageable) {

		QTodo todo = QTodo.todo;
		QManager manager = QManager.manager;
		QComment comment = QComment.comment;

		JPQLQuery<TodoSearchResponse> query = queryFactory
			.select(Projections.constructor( // 생성자로 매핑
				TodoSearchResponse.class,
				todo.title, // 제목
				todo.managers.size().longValue(), // 담당자 수
				JPAExpressions.select(comment.count()) // 서브쿼리
					.from(comment)
					.where(comment.todo.eq(todo)) //댓글이 연결된 todo와 현재 todo랑 같은지 확인
			))
			.from(todo) // 메인 테이블
			.leftJoin(todo.managers, manager) //
			.where(
				title != null ? todo.title.containsIgnoreCase(title) : null, //containsIgnoreCase: LIKE 절 '% %' 의미
				nickName != null ? manager.user.nickName.containsIgnoreCase(nickName) : null,
				startDate != null ? todo.createdAt.goe(startDate) : null, // goe: >= 즉 시작일 이후
				endDate != null ? todo.createdAt.loe(endDate) : null // loe: <= 종료일 이전
			)
			.distinct() // 중복제거
			.offset(pageable.getOffset()) // 페이지 시작 인덱스
			.limit(pageable.getPageSize()); // 페이지당 보여줄 항목수

		List<TodoSearchResponse> result = query.fetch(); // 작성된 쿼리 실행
		long total = query.fetchCount(); // 전체 데이터 개수 조회

		return new PageImpl<>(result, pageable, total);

	}

}
