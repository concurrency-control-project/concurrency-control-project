package com.example.concurrencycontrolproject.domain.concert.repository;

import static com.example.concurrencycontrolproject.domain.concert.entity.QConcert.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ConcertSearchRepositoryImpl implements ConcertSearchRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Concert> search(String keyword, Pageable pageable) {

		List<Concert> concerts = queryFactory.selectFrom(concert)
			.where(
				isNotDeleted(),
				keywordContains(keyword)
			).orderBy(concert.concertStartDateTime.asc())
			.offset(pageable.getOffset()).limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(concert.count()).from(concert)
			.where(
				isNotDeleted(),
				keywordContains(keyword)
			);
		return PageableExecutionUtils.getPage(concerts, pageable, () -> countQuery.fetchOne());
	}

	private BooleanExpression keywordContains(String keyword) {
		if (!StringUtils.hasText(keyword)) {
			return null;
		}
		return concert.title.contains(keyword)
			.or(concert.performer.contains(keyword))
			.or(concert.location.contains(keyword));
	}

	private BooleanExpression isNotDeleted() {
		return concert.status.ne(ConcertStatus.DELETED);
	}

}
