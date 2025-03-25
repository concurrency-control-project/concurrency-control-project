package com.example.concurrencycontrolproject.domain.seat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.seat.dto.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.Seat;
import com.example.concurrencycontrolproject.domain.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatRepository seatRepository;

	public SeatResponseDTO createSeat(SeatRequestDTO dto) {
		Seat seat = new Seat(null, dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());
		Seat savedSeat = seatRepository.save(seat);
		return new SeatResponseDTO(savedSeat.getId(), savedSeat.getNumber(), savedSeat.getGrade(),
			savedSeat.getPrice(), savedSeat.getSection());
	}

	public List<SeatResponseDTO> getAllSeats() {
		return seatRepository.findAll()
			.stream()
			.map(seat -> new SeatResponseDTO(seat.getId(), seat.getNumber(), seat.getGrade(),
				seat.getPrice(), seat.getSection()))
			.collect(Collectors.toList());
	}
}
