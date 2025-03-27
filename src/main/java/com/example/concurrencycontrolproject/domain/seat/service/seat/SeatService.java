package com.example.concurrencycontrolproject.domain.seat.service.seat;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.common.response.PageResponse;
import com.example.concurrencycontrolproject.domain.common.response.Response;
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

	public Response<SeatResponseDTO> createSeat(SeatRequestDTO dto) {

		if (dto.getNumber() <= 0 || dto.getPrice() < 0) {
			throw new SeatException(SeatErrorCode.INVALID_SEAT_DATA);
		}
		Seat seat = new Seat(null, dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());
		try {
			Seat savedSeat = seatRepository.save(seat);
			SeatResponseDTO responseDTO = new SeatResponseDTO(savedSeat.getId(), savedSeat.getNumber(),
				savedSeat.getGrade(),
				savedSeat.getPrice(), savedSeat.getSection());
			return Response.of(responseDTO);
		} catch (Exception e) {
			throw new SeatException(SeatErrorCode.SEAT_CREATION_FAILED);
		}
	}

	public Response<PageResponse<SeatResponseDTO>> getAllSeats(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Seat> seatPage = seatRepository.findAll(pageable);

		PageResponse<SeatResponseDTO> pageResponse = new PageResponse<>(
			seatPage.getContent().stream()
				.map(seat -> new SeatResponseDTO(seat.getId(), seat.getNumber(), seat.getGrade(), seat.getPrice(),
					seat.getSection()))
				.collect(Collectors.toList()),
			seatPage.getNumber(),
			seatPage.getSize(),
			seatPage.getTotalPages(),
			seatPage.getTotalElements()
		);
		return Response.of(pageResponse);
	}

	public Response<SeatResponseDTO> getSeat(Long seatId) {
		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatErrorCode.SEAT_NOT_FOUND));
		return Response.of(toResponseDTO(seat));
	}

	public Response<SeatResponseDTO> updateSeat(Long seatId, SeatRequestDTO dto) {
		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatErrorCode.SEAT_NOT_FOUND));

		validateSeatData(dto);

		seat.update(dto.getNumber(), dto.getGrade(), dto.getPrice(), dto.getSection());
		Seat updatedSeat = seatRepository.save(seat);

		return Response.of(toResponseDTO(updatedSeat));
	}

	public Response<String> deleteSeat(Long seatId) {
		if (!seatRepository.existsById(seatId)) {
			throw new SeatException(SeatErrorCode.SEAT_NOT_FOUND);
		}
		seatRepository.deleteById(seatId);
		return Response.of("좌석 삭제 완료!");
	}

	private SeatResponseDTO toResponseDTO(Seat seat) {
		return new SeatResponseDTO(seat.getId(), seat.getNumber(), seat.getGrade(), seat.getPrice(), seat.getSection());
	}

	private void validateSeatData(SeatRequestDTO dto) {
		if (dto.getNumber() <= 0 || dto.getPrice() < 0) {
			throw new SeatException(SeatErrorCode.INVALID_SEAT_DATA);
		}
	}

}
