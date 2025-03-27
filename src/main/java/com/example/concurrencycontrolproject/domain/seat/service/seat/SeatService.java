package com.example.concurrencycontrolproject.domain.seat.service.seat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequest;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponse;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatRepository seatRepository;

	public Seat createSeat(SeatRequest dto) {
		Seat seat = Seat.of(dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());
		return seatRepository.save(seat);
	}

	// 좌석 목록 조회 메서드
	public Page<SeatResponse> getAllSeats(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return seatRepository.findAll(pageable).map(SeatResponse::fromSeat);
	}

	// 좌석 상세 조회 메서드
	public Seat getSeat(Long seatId) {
		return seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatErrorCode.SEAT_NOT_FOUND));
	}

	@Transactional
	public Seat updateSeat(Long seatId, SeatRequest dto) {
		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatErrorCode.SEAT_NOT_FOUND));

		seat.update(dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());

		return seatRepository.save(seat);
	}

	@Transactional
	public boolean deleteSeat(Long seatId) {
		if (!seatRepository.existsById(seatId)) {
			throw new SeatException(SeatErrorCode.SEAT_NOT_FOUND);
		}
		seatRepository.deleteById(seatId);
		return true;
	}
	//사고방지
}
