package com.example.concurrencycontrolproject.domain.canceledticket.repository;

import static com.example.concurrencycontrolproject.domain.canceledticket.entity.QCanceledTicket.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.canceledticket.dto.response.CanceledTicketResponse;
import com.example.concurrencycontrolproject.domain.canceledticket.entity.QCanceledTicket;
import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.QSeat;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CanceledTicketRepositoryImpl implements CanceledTicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CanceledTicketResponse> findCanceledTickets(Long userId, Pageable pageable, Long scheduleId,
		LocalDateTime startedAt, LocalDateTime endedAt) {

		QCanceledTicket canceledTicket = QCanceledTicket.canceledTicket;
		QSeat seat = QSeat.seat;

		List<CanceledTicketResponse> list = queryFactory
			.select(Projections.constructor(CanceledTicketResponse.class,
					canceledTicket.id,
					canceledTicket.scheduleId,
					canceledTicket.seatId,
					Projections.constructor(SeatResponseDto.class,
						seat.id,
						seat.number,
						seat.grade,
						seat.price,
						seat.section
					),
					canceledTicket.originalCreatedAt,
					canceledTicket.canceledAt
				)
			)
			.from(canceledTicket)
			.join(seat).on(canceledTicket.seatId.eq(seat.id))
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
				canceledDateBetween(startedAt, endedAt)
			)
			.orderBy(canceledTicket.canceledAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(canceledTicket.count())
			.from(canceledTicket)
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
				canceledDateBetween(startedAt, endedAt)
			)
			.fetchOne();

		return new PageImpl<>(list, pageable, count != null ? count : 0L);
	}

	private BooleanExpression userIdEq(Long userId) {
		return userId != null ? canceledTicket.userId.eq(userId) : null;
	}

	private BooleanExpression scheduleIdEq(Long scheduleId) {
		return scheduleId != null ? canceledTicket.scheduleId.eq(scheduleId) : null;
	}

	// 취소 시간 기준 필터링
	private BooleanExpression canceledDateBetween(LocalDateTime startedAt, LocalDateTime endedAt) {
		if (startedAt == null && endedAt == null) {
			return null;
		}
		if (startedAt == null) {
			return canceledTicket.canceledAt.loe(endedAt);
		}
		if (endedAt == null) {
			return canceledTicket.canceledAt.goe(startedAt);
		}
		return canceledTicket.canceledAt.between(startedAt, endedAt);
	}
}