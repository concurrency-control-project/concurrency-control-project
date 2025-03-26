package com.example.concurrencycontrolproject.seat;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.concurrencycontrolproject.domain.seat.dto.SeatRequestDTO;
import com.example.concurrencycontrolproject.domain.seat.dto.SeatResponseDTO;
import com.example.concurrencycontrolproject.domain.seat.entity.Seat;
import com.example.concurrencycontrolproject.domain.seat.repository.SeatRepository;
import com.example.concurrencycontrolproject.domain.seat.service.SeatService;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

	@Mock
	private SeatRepository seatRepository;

	@InjectMocks
	private SeatService seatService;

	private Seat seat1;
	private Seat seat2;

	@BeforeEach
	void setUp() {
		seat1 = new Seat(1L, 1, "VIP", 1000, "A");
		seat2 = new Seat(2L, 2, "VIP", 1000, "B");
	}

	@Test
	void testCreateSeat() {
		//given
		SeatRequestDTO requestDTO = new SeatRequestDTO(3, "S", 6000, "C");
		Seat savedSeat = new Seat(3L, requestDTO.getNumber(), requestDTO.getGrade(), requestDTO.getPrice(),
			requestDTO.getSection());
		when(seatRepository.save(any(Seat.class))).thenReturn(savedSeat);

		// when
		SeatResponseDTO responseDTO = seatService.createSeat(requestDTO);

		// then
		assertThat(responseDTO).isNotNull();
		assertThat(responseDTO.getId()).isEqualTo(3L);
		assertThat(responseDTO.getNumber()).isEqualTo(3);
		assertThat(responseDTO.getGrade()).isEqualTo("S");
		assertThat(responseDTO.getPrice()).isEqualTo(6000);
		assertThat(responseDTO.getSection()).isEqualTo("C");

		verify(seatRepository, times(1)).save(any(Seat.class));

	}

	@Test
	void testGetAllSeat() {

	}
}
