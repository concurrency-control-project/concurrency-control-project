package com.example.concurrencycontrolproject.domain.seat.controller.seat;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.common.response.PageResponse;
import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequest;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponse;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.service.seat.SeatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;

	// 좌석 생성 API
	@PostMapping("/v1/seats")
	public ResponseEntity<Response<SeatResponse>> createSeat(@RequestBody @Valid SeatRequest seatRequest) {
		Seat seat = seatService.createSeat(seatRequest);
		return ResponseEntity.ok(Response.of(toSeatResponse(seat)));
	}

	// 좌석 목록 조회 API
	@GetMapping("/v1/seats")
	public ResponseEntity<Response<PageResponse<SeatResponse>>> getAllSeats(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<Seat> seatPage = seatService.getAllSeats(page, size);

		PageResponse<SeatResponse> pageResponse = new PageResponse<>(
			seatPage.getContent().stream().map(this::toSeatResponse).collect(Collectors.toList()),
			seatPage.getNumber(),
			seatPage.getSize(),
			seatPage.getTotalPages(),
			seatPage.getTotalElements()
		);

		return ResponseEntity.ok(Response.of(pageResponse));
	}

	// 좌석 단건 조회 API
	@GetMapping("/v1/seats/{seatId}")
	public ResponseEntity<Response<SeatResponse>> getSeat(@PathVariable Long seatId) {
		Seat seat = seatService.getSeat(seatId);
		return ResponseEntity.ok(Response.of(toSeatResponse(seat)));
	}

	// 좌석 업데이트 API
	@PutMapping("/v1/seats/{seatId}")
	public ResponseEntity<Response<SeatResponse>> updateSeat(
		@PathVariable Long seatId,
		@RequestBody @Valid SeatRequest seatRequest
	) {
		Seat seat = seatService.updateSeat(seatId, seatRequest);
		return ResponseEntity.ok(Response.of(toSeatResponse(seat)));
	}

	@DeleteMapping("/v1/seats/{seatId}")
	public ResponseEntity<Response<String>> deleteSeat(@PathVariable Long seatId) {
		boolean isDeleted = seatService.deleteSeat(seatId);
		String message = isDeleted ? "좌석 삭제 완료!" : "좌석 삭제 실패";
		return ResponseEntity.ok(Response.of(message));
	}

	private SeatResponse toSeatResponse(Seat seat) {
		return new SeatResponse(seat.getId(), seat.getNumber(), seat.getGrade(), seat.getPrice(), seat.getSection());
	}

	@GetMapping("/{seatId}")
	public ResponseEntity<Response<SeatResponseDTO>> getSeat(@PathVariable Long seatId) {
		return ResponseEntity.ok(seatService.getSeat(seatId));
	}

	@PutMapping("/{seatId}")
	public ResponseEntity<Response<SeatResponseDTO>> updateSeat(
		@PathVariable Long seatId,
		@RequestBody SeatRequestDTO requestDTO
	) {
		return ResponseEntity.ok(seatService.updateSeat(seatId, requestDTO));
	}

	@DeleteMapping("/{seatId}")
	public ResponseEntity<Response<String>> deleteSeat(@PathVariable Long seatId) {
		return ResponseEntity.ok(seatService.deleteSeat(seatId));
	}
}


