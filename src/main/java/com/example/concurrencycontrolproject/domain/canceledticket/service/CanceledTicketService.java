package com.example.concurrencycontrolproject.domain.canceledticket.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.canceledticket.dto.response.CanceledTicketResponse;
import com.example.concurrencycontrolproject.domain.canceledticket.repository.CanceledTicketRepository;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CanceledTicketService {

	private final CanceledTicketRepository canceledTicketRepository;
	private final UserRepository userRepository;

	// 유저 검증
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new TicketException(TicketErrorCode.USER_NOT_FOUND));
	}

	// 취소된 티켓 다건 조회
	public Page<CanceledTicketResponse> getCanceledTickets(
		AuthUser authUser, Pageable pageable, Long scheduleId,
		LocalDateTime startedAt, LocalDateTime endedAt) {

		User user = findUser(authUser.getId());

		Pageable convertPageable = PageRequest.of(
			pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0,
			pageable.getPageSize()
		);

		return canceledTicketRepository.findCanceledTickets(
			user.getId(), convertPageable, scheduleId, startedAt, endedAt
		);

	}
}