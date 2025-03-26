package com.example.concurrencycontrolproject.domain.seat.service.seat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatRepository seatRepository;

	public SeatResponseDTO createSeat(SeatRequestDTO dto) {

		if (dto.getNumber() <= 0 || dto.getPrice() < 0) {
			throw new SeatException(SeatErrorCode.INVALID_SEAT_DATA);
		}
		Seat seat = new Seat(null, dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());
		try {
			Seat savedSeat = seatRepository.save(seat);
			return new SeatResponseDTO(savedSeat.getId(), savedSeat.getNumber(), savedSeat.getGrade(),
				savedSeat.getPrice(), savedSeat.getSection());
		} catch (Exception e) {
			throw new SeatException(SeatErrorCode.SEAT_CREATION_FAILED);
		}
	}

	public List<SeatResponseDTO> getAllSeats() {
		List<Seat> seats = seatRepository.findAll();
		if (seats.isEmpty()) {
			throw new SeatException(SeatErrorCode.SEAT_NOT_FOUND);
		}
		return seats.stream()
			.map(seat -> new SeatResponseDTO(seat.getId(), seat.getNumber(), seat.getGrade(),
				seat.getPrice(), seat.getSection()))
			.collect(Collectors.toList());
	}
}
