package com.example.concurrencycontrolproject.domain.Ticket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.scheduleSeat.response.ScheduleSeatRepository;
import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;
import com.example.concurrencycontrolproject.domain.seat.entity.Seat;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

	@InjectMocks
	private TicketService ticketService;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ScheduleSeatRepository scheduleSeatRepository;

	@Mock
	private Ticket mockTicket;

	@Mock
	private User mockUser;

	@Mock
	private Seat mockSeat;

	@Mock
	private Schedule mockSchedule;

	@Mock
	private ScheduleSeat mockScheduleSeat;

	// 공용 필드
	private Long ticketId, userId, seatId, scheduleSeatId, scheduleId;

	// 각 테스트 실행 전 선행 실행
	@BeforeEach
	void setUp() {
		// 유저
		userId = 1L;
		ReflectionTestUtils.setField(mockUser, "id", userId);

		//시트
		seatId = 1L;
		ReflectionTestUtils.setField(mockSeat, "id", seatId);
		ReflectionTestUtils.setField(mockSeat, "number", 1);
		ReflectionTestUtils.setField(mockSeat, "grade", "S석");
		ReflectionTestUtils.setField(mockSeat, "price", 100000);
		ReflectionTestUtils.setField(mockSeat, "section", "A열");

		// 스케줄
		scheduleId = 1L;
		ReflectionTestUtils.setField(mockSchedule, "id", scheduleId);

		// 스케줄시트
		scheduleSeatId = 1L;
		ReflectionTestUtils.setField(mockScheduleSeat, "id", scheduleSeatId);
		ReflectionTestUtils.setField(mockScheduleSeat, "isAssigned", false);
		ReflectionTestUtils.setField(mockScheduleSeat, "seat", mockSeat);
		ReflectionTestUtils.setField(mockScheduleSeat, "schedule", mockSchedule);

		// 티켓
		ticketId = 1L;
		ReflectionTestUtils.setField(mockTicket, "id", ticketId);
		ReflectionTestUtils.setField(mockTicket, "scheduleSeat", mockScheduleSeat);

	}

	// saveTicket 테스트
	@Test
	void saveTicket_성공_시_Null_을_반환하지_않고_필드값이_일치한다() {
		// given
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(mockUser));
		when(scheduleSeatRepository.findByIdAndAssignedIsFalse(scheduleSeatId))
			.thenReturn(Optional.of(mockScheduleSeat));

		when(ticketRepository.save(any(Ticket.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// 연쇄 참조 mocking
		when(mockScheduleSeat.getSeat())
			.thenReturn(mockSeat);
		when(mockScheduleSeat.getSchedule())
			.thenReturn(mockSchedule);

		// when
		TicketResponseDto ticketResponseDto = ticketService.saveTicket(userId, scheduleSeatId);

		// then
		assertNotNull(ticketResponseDto);
		assertEquals(mockScheduleSeat.getSeat().getId(), ticketResponseDto.getSeat().getId());
		assertEquals(TicketStatus.RESERVED, ticketResponseDto.getStatus());
		assertEquals(mockSeat.getId(), ticketResponseDto.getSeat().getId());
		assertEquals(mockSeat.getPrice(), ticketResponseDto.getSeat().getPrice());
		assertEquals(mockSeat.getSection(), ticketResponseDto.getSeat().getSection());
		assertEquals(mockSeat.getNumber(), ticketResponseDto.getSeat().getNumber());
		verify(mockScheduleSeat).assign();

	}

	@Test
	void getTicket_성공_시_티켓을_반환한다() {

		// given
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(mockUser));
		when(ticketRepository.findById(ticketId))
			.thenReturn(Optional.of(mockTicket));

		when(mockTicket.getScheduleSeat())
			.thenReturn(mockScheduleSeat);
		when(mockScheduleSeat.getSeat())
			.thenReturn(mockSeat);
		when(mockScheduleSeat.getSchedule())
			.thenReturn(mockSchedule);

		// when
		TicketResponseDto ticketResponseDto = ticketService.getTicket(userId, ticketId);

		// then
		assertNotNull(ticketResponseDto);
		assertEquals(mockScheduleSeat.getSeat().getId(), ticketResponseDto.getSeat().getId());
		// assertEquals(TicketStatus.RESERVED, ticketResponseDto.getStatus());
		assertEquals(mockScheduleSeat.getSeat().getId(), ticketResponseDto.getSeat().getId());
		assertEquals(mockSeat.getPrice(), ticketResponseDto.getSeat().getPrice());
		assertEquals(mockSeat.getSection(), ticketResponseDto.getSeat().getSection());
		assertEquals(mockSeat.getNumber(), ticketResponseDto.getSeat().getNumber());
	}

	@Test
	void getTickets_성공_시_티켓_페이지를_반환한다() {

		// given
		String ticketStatus = TicketStatus.RESERVED.name();
		LocalDateTime startedAt = LocalDateTime.of(2023, 1, 1, 0, 0);
		LocalDateTime endedAt = LocalDateTime.of(2025, 12, 31, 23, 59);

		Pageable pageable = PageRequest.of(
			1,
			10,
			Sort.by("createdAt").descending());

		Pageable convertPageable = PageRequest.of(
			pageable.getPageNumber() - 1,
			pageable.getPageSize(),
			pageable.getSort()
		);

		when(userRepository.findById(userId))
			.thenReturn(Optional.of(mockUser));

		TicketResponseDto mockTicketResponseDto = TicketResponseDto.builder()
			.id(1L)
			.scheduleId(scheduleId)
			.status(TicketStatus.RESERVED)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.seat(SeatResponseDto.builder()
				.id(mockSeat.getId())
				.number(mockSeat.getNumber())
				.grade(mockSeat.getGrade())
				.price(mockSeat.getPrice())
				.section(mockSeat.getSection())
				.build())
			.build();

		Page<TicketResponseDto> mockPage = new PageImpl<>(
			List.of(mockTicketResponseDto),
			PageRequest.of(0, 10, Sort.by("createdAt").descending()),
			1);

		when(ticketRepository.findTickets(eq(userId), eq(convertPageable), eq(scheduleId),
			eq(ticketStatus), eq(startedAt), eq(endedAt))).thenReturn(mockPage);

		// when
		Page<TicketResponseDto> result = ticketService.getTickets(userId, pageable, scheduleId,
			ticketStatus, startedAt, endedAt);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(mockTicketResponseDto.getId(), result.getContent().get(0).getId());
	}

	@Test
	void deleteTicket_성공한다() {
		// given
		when(userRepository.findById(userId))
			.thenReturn(Optional.of(mockUser));

		mockScheduleSeat = org.mockito.Mockito.mock(ScheduleSeat.class);
		mockTicket = Ticket.saveTicket(mockScheduleSeat);
		when(ticketRepository.findById(ticketId))
			.thenReturn(Optional.of(mockTicket));

		// when
		ticketService.deleteTicket(userId, ticketId);

		// then
		assertEquals(TicketStatus.CANCELED, mockTicket.getStatus());
		verify(ticketRepository).save(eq(mockTicket));
	}
}
