package com.example.concurrencycontrolproject.seat.controllerTest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.concurrencycontrolproject.domain.common.response.Response;
import com.example.concurrencycontrolproject.domain.seat.controller.scheduledSeat.ScheduledSeatController;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatRequest;
import com.example.concurrencycontrolproject.domain.seat.dto.scheduledSeat.ScheduledSeatResponse;
import com.example.concurrencycontrolproject.domain.seat.service.scheduledSeat.ScheduledSeatService;

public class ScheduledSeatControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	private ScheduledSeatController scheduledSeatController;

	@Mock
	private ScheduledSeatService scheduledSeatService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(scheduledSeatController).build();
	}

	@Test
	public void testReserveSeat() throws Exception {
		// Given
		ScheduledSeatRequest requestDTO = new ScheduledSeatRequest();
		requestDTO.setScheduleId(1L);
		requestDTO.setSeatId(1L);

		ScheduledSeatResponse responseDTO = new ScheduledSeatResponse("redisKey", 1L, 1L, true, 1L);
		Response<ScheduledSeatResponse> response = Response.of(responseDTO);

		// When
		when(scheduledSeatService.reserveSeat(anyLong(), anyLong(), anyLong())).thenReturn(response);

		// Then
		mockMvc.perform(post("/api/v1/scheduled-seats/v1/scheduled-seats")
				.contentType("application/json")
				.content("{\"scheduleId\":1,\"seatId\":1,\"userId\":1}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.scheduleId").value(1L))
			.andExpect(jsonPath("$.data.seatId").value(1L))
			.andExpect(jsonPath("$.data.reservedBy").value(1L));
	}

	@Test
	public void testCancelReservation() throws Exception {
		// Given
		ScheduledSeatResponse responseDTO = new ScheduledSeatResponse("redisKey", 1L, 1L, false, null);
		Response<ScheduledSeatResponse> response = Response.of(responseDTO);

		// When
		when(scheduledSeatService.cancelReservation(anyLong(), anyLong())).thenReturn(response);

		// Then
		mockMvc.perform(delete("/api/v1/scheduled-seats/v1/scheduled-seat/1/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.scheduleId").value(1L))
			.andExpect(jsonPath("$.data.seatId").value(1L))
			.andExpect(jsonPath("$.data.isAssigned").value(false));
	}

	@Test
	public void testGetReservation() throws Exception {
		// Given
		ScheduledSeatResponse responseDTO = new ScheduledSeatResponse("redisKey", 1L, 1L, true, 1L);
		Response<ScheduledSeatResponse> response = Response.of(responseDTO);

		// When
		when(scheduledSeatService.getReservation(anyLong(), anyLong())).thenReturn(response);

		// Then
		mockMvc.perform(get("/api/v1/scheduled-seats/v1/scheduled-seats/1/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.scheduleId").value(1L))
			.andExpect(jsonPath("$.data.seatId").value(1L))
			.andExpect(jsonPath("$.data.reservedBy").value(1L));
	}
}
