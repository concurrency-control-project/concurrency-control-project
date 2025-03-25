package com.example.concurrencycontrolproject.domain.ticket.repository;

import static com.example.concurrencycontrolproject.domain.scheduleSeat.entity.QScheduleSeat.*;
import static com.example.concurrencycontrolproject.domain.seat.entity.QSeat.*;
import static com.example.concurrencycontrolproject.domain.ticket.entity.QTicket.*;
import static com.example.concurrencycontrolproject.domain.user.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.entity.QTicket;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<TicketResponseDto> findTickets(Long userId, Pageable pageable, Long scheduleId,
		String scheduleStatus,
		String ticketStatus, LocalDateTime startedAt, LocalDateTime endedAt) {

		QTicket ticket = QTicket.ticket;

		List<TicketResponseDto> list = queryFactory
			.select(Projections.constructor(TicketResponseDto.class,
					ticket.id,
					ticket.scheduleSeat.schedule.id,
					ticket.status,
					ticket.createdAt,
					ticket.modifiedAt,
					Projections.constructor(SeatResponseDto.class,
						ticket.scheduleSeat.seat.id,
						ticket.scheduleSeat.seat.number,
						ticket.scheduleSeat.seat.grade,
						ticket.scheduleSeat.seat.price,
						ticket.scheduleSeat.seat.section
					)
				)
			)
			.from(ticket)
			.join(ticket.scheduleSeat, scheduleSeat)
			.join(scheduleSeat.seat, seat)
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
				scheduleStatusEq(scheduleStatus),
				ticketStatusEq(ticketStatus),
				dateBetween(startedAt, endedAt)
			)
			.orderBy(ticket.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(ticket.count())
			.from(ticket)
			.join(ticket.scheduleSeat, scheduleSeat)
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
				scheduleStatusEq(scheduleStatus),
				ticketStatusEq(ticketStatus),
				dateBetween(startedAt, endedAt)
			)
			.fetchOne();

		return new PageImpl<>(list, pageable, count != null ? count : 0L);
	}

	private BooleanExpression userIdEq(Long userId) {
		return userId != null ? user.id.eq(userId) : null;
	}

	private BooleanExpression scheduleIdEq(Long scheduleId) {
		return scheduleId != null ? ticket.scheduleSeat.id.eq(scheduleId) : null;
	}

	private BooleanExpression scheduleStatusEq(String scheduleStatus) {
		return scheduleStatus != null ? scheduleSeat.schedule.status.eq(scheduleStatus) : null;
	}

	private BooleanExpression ticketStatusEq(String ticketStatus) {
		return ticketStatus != null ? ticket.status.stringValue().eq(ticketStatus) : null;
	}

	private BooleanExpression dateBetween(LocalDateTime startedAt, LocalDateTime endedAt) {
		if (startedAt == null && endedAt == null) {
			return null;
		}
		if (startedAt == null) {
			return ticket.createdAt.loe(endedAt);
		}
		if (endedAt == null) {
			return ticket.createdAt.goe(startedAt);
		}
		return ticket.createdAt.between(startedAt, endedAt);
	}

}
