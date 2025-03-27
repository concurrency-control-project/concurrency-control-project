package com.example.concurrencycontrolproject.domain.ticket.repository;

import static com.example.concurrencycontrolproject.domain.scheduleSeat.entity.QScheduleSeat.*;
import static com.example.concurrencycontrolproject.domain.seat.entity.seat.QSeat.*;
import static com.example.concurrencycontrolproject.domain.ticket.entity.QTicket.*;
import static com.example.concurrencycontrolproject.domain.userTicket.entity.QUserTicket.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponse;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.example.concurrencycontrolproject.domain.ticket.entity.QTicket;
import com.example.concurrencycontrolproject.domain.user.entity.QUser;
import com.example.concurrencycontrolproject.domain.userTicket.entity.QUserTicket;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<TicketResponse> findTickets(Long userId, Pageable pageable, Long scheduleId,
		String ticketStatus, LocalDateTime startedAt, LocalDateTime endedAt) {

		QTicket ticket = QTicket.ticket;
		QUserTicket userTicket = QUserTicket.userTicket;
		QUser user = QUser.user;

		List<TicketResponse> list = queryFactory
			.select(Projections.constructor(TicketResponse.class,
					ticket.id,
					ticket.scheduleSeat.schedule.id,
					ticket.status,
					ticket.createdAt,
					ticket.modifiedAt,
					Projections.constructor(SeatResponse.class,
						ticket.scheduleSeat.seat.id,
						ticket.scheduleSeat.seat.number,
						ticket.scheduleSeat.seat.grade,
						ticket.scheduleSeat.seat.price,
						ticket.scheduleSeat.seat.section
					)
				)
			)
			.from(ticket)
			.join(userTicket).on(userTicket.ticket.id.eq(ticket.id))
			.join(userTicket.user, user)
			.join(ticket.scheduleSeat, scheduleSeat)
			.join(scheduleSeat.seat, seat)
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
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
			.join(userTicket).on(userTicket.ticket.id.eq(ticket.id))
			.join(userTicket.user, user)
			.join(ticket.scheduleSeat, scheduleSeat)
			.join(scheduleSeat.seat, seat)
			.where(
				userIdEq(userId),
				scheduleIdEq(scheduleId),
				ticketStatusEq(ticketStatus),
				dateBetween(startedAt, endedAt)
			)
			.fetchOne();

		return new PageImpl<>(list, pageable, count != null ? count : 0L);
	}

	private BooleanExpression userIdEq(Long userId) {
		return userId != null ? userTicket.user.id.eq(userId) : null;
	}

	private BooleanExpression scheduleIdEq(Long scheduleId) {
		return scheduleId != null ? ticket.scheduleSeat.id.eq(scheduleId) : null;
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
