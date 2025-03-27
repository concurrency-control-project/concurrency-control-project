package com.example.concurrencycontrolproject.seat.serviceTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import com.example.concurrencycontrolproject.domain.common.response.PageResponse;
import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.Seat.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatErrorCode;
import com.example.concurrencycontrolproject.domain.seat.exception.seat.SeatException;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;
import com.example.concurrencycontrolproject.domain.seat.service.seat.SeatService;

@ExtendWith(MockitoExtension.class) // Mockito 확장 사용
public class SeatServiceTest {

	@Mock
	private SeatRepository seatRepository; // Mock 객체 생성

	@InjectMocks
	private SeatService seatService; // 테스트할 서비스 객체

	private Seat seat;

	@BeforeEach
	void setUp() {
		seat = new Seat(1L, 1, "VIP", 100000, "A구역");
	}

	@Test
	void testCreateSeat_Success() {
		// Given (SeatRequestDTO 설정)
		SeatRequestDTO requestDTO = new SeatRequestDTO(1, "VIP", 100000, "A구역");

		// Stub 설정 (seatRepository.save()가 실행되면 seat 반환)
		when(seatRepository.save(any(Seat.class))).thenReturn(seat);

		// When (서비스 메서드 호출)
		Response<SeatResponseDTO> response = seatService.createSeat(requestDTO);

		// Then (검증)
		assertThat(response.getData()).isNotNull();
		assertThat(response.getData().getId()).isEqualTo(1L);
		assertThat(response.getData().getNumber()).isEqualTo(1);
		assertThat(response.getData().getGrade()).isEqualTo("VIP");
		assertThat(response.getData().getPrice()).isEqualTo(100000);
		assertThat(response.getData().getSection()).isEqualTo("A구역");

		// seatRepository.save()가 1번 호출되었는지 검증
		verify(seatRepository, times(1)).save(any(Seat.class));
	}

	@Test
	void testCreateSeat_Fail_WhenInvalidData() {
		// Given: 잘못된 SeatRequestDTO (음수 값)
		SeatRequestDTO requestDTO = new SeatRequestDTO(-1, "VIP", -50000, "A구역");

		// When & Then
		SeatException exception = assertThrows(
			SeatException.class,
			() -> seatService.createSeat(requestDTO)
		);

		assertThat(exception.getMessage()).isEqualTo(SeatErrorCode.INVALID_SEAT_DATA.getDefaultMessage());
		verify(seatRepository, never()).save(any(Seat.class));
	}

	@Test
	void testGetAllSeats_Success() {
		// Given
		int page = 0, size = 5;
		List<Seat> seatList = IntStream.range(1, 6)
			.mapToObj(i -> new Seat((long)i, i, "VIP", 100000, "A구역"))
			.collect(Collectors.toList());
		Page<Seat> seatPage = new PageImpl<>(seatList, PageRequest.of(page, size), seatList.size());

		when(seatRepository.findAll(any(Pageable.class))).thenReturn(seatPage);

		// When
		Response<PageResponse<SeatResponseDTO>> response = seatService.getAllSeats(page, size);

		// Then
		assertThat(response.getData()).isNotNull();
		assertThat(response.getData().getData()).hasSameClassAs(5);
		assertThat(response.getData().getTotalPages()).isEqualTo(1);
		assertThat(response.getData().getTotalElements()).isEqualTo(5);

		// seatRepository.findAll()이 1번 호출되었는지 검증
		verify(seatRepository, times(1)).findAll(any(Pageable.class));
	}
}
