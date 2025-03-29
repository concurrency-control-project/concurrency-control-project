package com.example.concurrencycontrolproject.domain.concert.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.concert.dto.request.ConcertRequest;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertDetailResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertResponse;
import com.example.concurrencycontrolproject.domain.concert.dto.response.ConcertSearchResponse;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.example.concurrencycontrolproject.domain.concert.exception.CannotDeleteConcertException;
import com.example.concurrencycontrolproject.domain.concert.exception.ConcertAlreadyExistsException;
import com.example.concurrencycontrolproject.domain.concert.exception.ConcertNotFoundException;
import com.example.concurrencycontrolproject.domain.concert.exception.InvalidBookingPeriodException;
import com.example.concurrencycontrolproject.domain.concert.exception.InvalidConcertPeriodException;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.concert.support.ConcertTestBuilder;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.service.ScheduleReader;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

	@Mock
	private ConcertRepository concertRepository;

	@Mock
	private ScheduleReader scheduleReader;

	@Mock
	private ConcertReader concertReader;

	@InjectMocks
	private ConcertService concertService;

	@Nested
	class CreateConcert {

		@Test
		public void 콘서트_생성_콘서트_종요일이_시작일보다_빠르면_InvalidConcertPeriodException_이_발생한다() {

			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 20, 0, 0, 0);
			LocalDateTime concertEndDateTime = LocalDateTime.of(2025, 3, 19, 0, 0, 0);

			//given
			ConcertRequest concertRequest = defaultTestBuilder().withConcertStartDateTime(concertStartDateTime)
				.withConcertEndDateTime(concertEndDateTime)
				.buildConcertRequest();

			//when & then
			assertThatThrownBy(() -> concertService.createConcert(concertRequest)).isInstanceOf(
				InvalidConcertPeriodException.class).hasMessage("콘서트 종료일은 콘서트 시작일보다 이후여야 합니다.");
		}

		@Test
		public void 콘서트_생성_콘서트_예약종료일이_예약시작일보다_빠르면_InvalidBookingPeriodException_이_발생한다() {

			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 25, 0, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 24, 0, 0, 0);

			//given
			ConcertRequest concertRequest = defaultTestBuilder()
				.withBookingStartDateTime(bookingStartDateTime)
				.withBookingEndDateTime(bookingEndDateTime)
				.buildConcertRequest();

			//when & then
			assertThatThrownBy(() -> concertService.createConcert(concertRequest)).isInstanceOf(
				InvalidBookingPeriodException.class).hasMessage("콘서트 예약종료일은 예약시작일보다 이후여야 합니다.");
		}

		@Test
		public void 콘서트_생성_콘서트시작일이_콘서트_예약_종료_보다_빠르면_InvalidBookingPeriodException_이_발생한다() {

			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 20, 0, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 21, 0, 0, 0);

			//given
			ConcertRequest concertRequest = defaultTestBuilder()
				.withBookingEndDateTime(bookingEndDateTime)
				.withConcertStartDateTime(concertStartDateTime)
				.buildConcertRequest();

			//when & then
			assertThatThrownBy(() -> concertService.createConcert(concertRequest)).isInstanceOf(
				InvalidBookingPeriodException.class).hasMessage("콘서트 예약 종료일은 콘서트 시작일보다 이전이여야 합니다.");
		}

		@Test
		public void 콘서트_생성_같은_콘서트_시작일과_같은_장소에_이미_등록된_콘서트가_있으면_ConcertAlreadyExistsException_이_발생한다() {

			//given
			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 20, 0, 0, 0);
			String location = "올림픽 공원";

			ConcertRequest concertRequest = defaultTestBuilder()
				.withConcertStartDateTime(concertStartDateTime)
				.withLocation(location)
				.buildConcertRequest();

			given(concertRepository.existsByConcertStartDateTimeAndLocation(any(LocalDateTime.class),
				any(String.class))).willReturn(true);
			//when & then
			assertThatThrownBy(() -> concertService.createConcert(concertRequest)).isInstanceOf(
				ConcertAlreadyExistsException.class).hasMessage("이미 같은 시간과 장소에 등록된 콘서트가 있습니다.");
		}

		@Test
		public void 콘서트_생성_모든_입력을_정상적으로_입력하면_등록된다() {

			ConcertTestBuilder concertTestBuilder = defaultTestBuilder();
			ConcertRequest concertRequest = concertTestBuilder.buildConcertRequest();
			Concert concert = concertTestBuilder.buildConcert();

			//given
			given(concertRepository.existsByConcertStartDateTimeAndLocation(any(LocalDateTime.class),
				any(String.class))).willReturn(false);
			given(concertRepository.save(any(Concert.class))).willReturn(concert);

			//when
			ConcertResponse result = concertService.createConcert(concertRequest);
			//then
			assertThat(result).extracting("id", "title", "performer", "description", "concertStartDateTime",
					"concertEndDateTime", "bookingStartDateTime", "bookingEndDateTime", "location", "status")
				.containsExactly(1L,
					"아이유콘서트",
					"아이유",
					"단독 콘서트",
					LocalDateTime.of(2025, 3, 20, 0, 0, 0),
					LocalDateTime.of(2025, 3, 24, 0, 0, 0),
					LocalDateTime.of(2025, 3, 10, 0, 0, 0),
					LocalDateTime.of(2025, 3, 20, 0, 0, 0),
					"올림픽 공원",
					ConcertStatus.PLANNED);
		}

	}

	@Nested
	class SearchConcerts {

		@Test
		public void 공연_검색_검색결과_응답으로_반환한다() {
			//given
			String keyword = "올림픽 공원";
			Pageable pageable = PageRequest.of(0, 10);

			Concert concert = defaultTestBuilder().buildConcert();
			Page<Concert> concerts = new PageImpl<>(List.of(concert), pageable, 1);
			given(concertRepository.search(any(String.class), any(Pageable.class))).willReturn(
				concerts);

			//when
			Page<ConcertSearchResponse> result = concertService.searchConcerts(keyword, pageable);
			//then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0)).extracting(
				"title",
				"performer",
				"location"
			).containsExactly(
				"아이유콘서트",
				"아이유",
				"올림픽 공원"
			);

			then(concertRepository).should().search(any(), any());
		}

		@Test
		public void 공연_검색_등록된_공연이_없으면_빈_배열을_반환한다() {
			//given
			String keyword = "";
			Pageable pageable = PageRequest.of(0, 10);
			Page<Concert> concerts = new PageImpl<>(List.of(), pageable, 0);
			given(concertRepository.search(any(String.class), any(Pageable.class))).willReturn(
				concerts);
			//when
			Page<ConcertSearchResponse> result = concertService.searchConcerts(keyword, pageable);
			//then
			assertThat(result.getContent()).isEmpty();
		}
	}

	@Nested
	class GetConcert {

		@Test
		public void 콘서트_상세_조회_없는_콘서트_조회시_ConcertNotFoundException_예외_발생한다() {
			//given
			Long concertId = 1L;
			given(concertReader.readNotDeleted(any(Long.class))).willThrow(ConcertNotFoundException.class);
			//when & then
			assertThatThrownBy(() -> concertService.getConcertWithSchedules(concertId)).isInstanceOf(
				ConcertNotFoundException.class);
		}

		@Test
		public void 콘서트_상세_스케줄_정보_포함해서_조회_성공() {
			//given
			Long concertId = 1L;
			Concert concert = defaultTestBuilder().buildConcert();
			LocalDateTime scheduleDateTime = LocalDateTime.now();
			Schedule schedule = Schedule.of(concert, scheduleDateTime, ScheduleStatus.ACTIVE);
			ReflectionTestUtils.setField(schedule, "id", 1L);
			given(concertReader.readNotDeleted(any(Long.class))).willReturn(concert);
			given(scheduleReader.readActiveSchedulesBy(any(Long.class))).willReturn(List.of(schedule));
			//when
			ConcertDetailResponse result = concertService.getConcertWithSchedules(concertId);
			//then
			assertThat(result.getScheduleResponses()).hasSize(1);
			assertThat(result.getScheduleResponses().get(0))
				.extracting(
					"id",
					"concertId",
					"dateTime",
					"status"
				).containsExactly(
					1L,
					concertId,
					scheduleDateTime,
					ScheduleStatus.ACTIVE
				);
		}

		@Test
		public void 콘서트_상세_조회_콘서트_기본정보_조회_성공() {
			//given
			Long concertId = 1L;
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.readNotDeleted(any(Long.class))).willReturn(concert);
			//when
			ConcertDetailResponse result = concertService.getConcertWithSchedules(concertId);
			//then
			assertThat(result).extracting(
				"id", "title", "location", "description",
				"concertStartDateTime",
				"concertEndDateTime"
			).containsExactly(
				1L, "아이유콘서트", "올림픽 공원", "단독 콘서트",
				LocalDateTime.of(2025, 3, 20, 0, 0),
				LocalDateTime.of(2025, 3, 24, 0, 0)
			);
		}
	}

	@Nested
	class UpdateConcert {

		@Test
		public void 콘서트_수정_콘서트_종료일이_시작일보다_빠르면_InvalidConcertPeriodException_이_발생한다() {
			//given
			Long concertId = 1L;
			LocalDateTime updateConcertStartDateTime = LocalDateTime.of(2025, 3, 25, 0, 0, 0);
			LocalDateTime updateConcertEndDateTime = LocalDateTime.of(2025, 3, 24, 0, 0, 0);
			ConcertRequest request = defaultTestBuilder()
				.withConcertStartDateTime(updateConcertStartDateTime)
				.withConcertEndDateTime(updateConcertEndDateTime)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			//when
			//then
			assertThatThrownBy(() -> concertService.updateConcert(concertId, request)).isInstanceOf(
				InvalidConcertPeriodException.class
			).hasMessage("콘서트 종료일은 콘서트 시작일보다 이후여야 합니다.");
		}

		@Test
		public void 콘서트_수정_콘서트_예약종료일이_예약시작일보다_빠르면_InvalidBookingPeriodException_이_발생한다() {
			//given
			Long concertId = 1L;
			LocalDateTime updateBookingStartDateTime = LocalDateTime.of(2025, 3, 20, 0, 0, 0);
			LocalDateTime updateBookingEndDateTime = LocalDateTime.of(2025, 3, 19, 0, 0, 0);
			ConcertRequest request = defaultTestBuilder()
				.withBookingStartDateTime(updateBookingStartDateTime)
				.withBookingEndDateTime(updateBookingEndDateTime)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			//when
			//then
			assertThatThrownBy(() -> concertService.updateConcert(concertId, request)).isInstanceOf(
				InvalidBookingPeriodException.class
			).hasMessage("콘서트 예약종료일은 예약시작일보다 이후여야 합니다.");
		}

		@Test
		public void 콘서트_수정_콘서트시작일이_콘서트_예약_종료_보다_빠르면_InvalidBookingPeriodException_이_발생한다() {
			//given
			Long concertId = 1L;
			LocalDateTime updateConcertStartDateTime = LocalDateTime.of(2025, 3, 20, 0, 0, 0);
			LocalDateTime updateBookingEndDateTime = LocalDateTime.of(2025, 3, 21, 0, 0, 0);
			ConcertRequest request = defaultTestBuilder()
				.withConcertStartDateTime(updateConcertStartDateTime)
				.withBookingEndDateTime(updateBookingEndDateTime)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			//when
			//then
			assertThatThrownBy(() -> concertService.updateConcert(concertId, request)).isInstanceOf(
				InvalidBookingPeriodException.class
			).hasMessage("콘서트 예약 종료일은 콘서트 시작일보다 이전이여야 합니다.");
		}

		@Test
		public void 콘서트_수정_장소_수정시_같은_콘서트_시작일과_같은_장소에_이미_등록된_콘서트가_있으면_ConcertAlreadyExistsException_이_발생한다() {
			//given
			Long concertId = 1L;
			String updateLocation = "수정된 장소";
			ConcertRequest request = defaultTestBuilder()
				.withLocation(updateLocation)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			given(concertRepository.existsByConcertStartDateTimeAndLocationAndIdNot(any(LocalDateTime.class),
				any(String.class), anyLong()))
				.willReturn(true);
			//when
			//then
			assertThatThrownBy(() -> concertService.updateConcert(concertId, request)).isInstanceOf(
				ConcertAlreadyExistsException.class
			).hasMessage("이미 같은 시간과 장소에 등록된 콘서트가 있습니다.");
		}

		@Test
		public void 콘서트_수정_콘서트_시작일_수정시_같은_콘서트_시작일과_같은_장소에_이미_등록된_콘서트가_있으면_ConcertAlreadyExistsException_이_발생한다() {
			//given
			Long concertId = 1L;
			LocalDateTime updateConcertStartDateTime = LocalDateTime.of(2025, 3, 21, 0, 0, 0);
			ConcertRequest request = defaultTestBuilder()
				.withConcertStartDateTime(updateConcertStartDateTime)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			given(concertRepository.existsByConcertStartDateTimeAndLocationAndIdNot(any(LocalDateTime.class),
				any(String.class), anyLong()))
				.willReturn(true);
			//when
			//then
			assertThatThrownBy(() -> concertService.updateConcert(concertId, request)).isInstanceOf(
				ConcertAlreadyExistsException.class
			).hasMessage("이미 같은 시간과 장소에 등록된 콘서트가 있습니다.");
		}

		@Test
		public void 콘서트_수정_모든_정보를_수정할_수_있다_성공() {
			//given
			Long concertId = 1L;
			String updateTitle = "수정된 타이틀";
			String updatePerformer = "수정된 아이유";
			String updateDescription = "수정된 콘서트";
			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 21, 0, 0);
			LocalDateTime concertEndDateTime = LocalDateTime.of(2025, 3, 25, 0, 0);
			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 11, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 21, 0, 0);
			String updateLocation = "수정된 올림픽 공원";
			ConcertRequest request = defaultTestBuilder()
				.withTitle(updateTitle)
				.withPerformer(updatePerformer)
				.withDescription(updateDescription)
				.withConcertStartDateTime(concertStartDateTime)
				.withConcertEndDateTime(concertEndDateTime)
				.withBookingStartDateTime(bookingStartDateTime)
				.withBookingEndDateTime(bookingEndDateTime)
				.withLocation(updateLocation)
				.buildConcertRequest();
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.read(anyLong())).willReturn(concert);
			//when
			concertService.updateConcert(concertId, request);
			//then
			assertThat(concert).extracting("id", "title", "performer", "description", "concertStartDateTime",
					"concertEndDateTime", "bookingStartDateTime", "bookingEndDateTime", "location")
				.containsExactly(1L,
					updateTitle,
					updatePerformer,
					updateDescription,
					concertStartDateTime,
					concertEndDateTime,
					bookingStartDateTime,
					bookingEndDateTime,
					updateLocation
				);
		}
	}

	@Nested
	class DeleteConcert {

		@Test
		public void 콘서트_삭제_상태가_예매전이_아니면_CannotDeleteConcertException이_발생한다() {
			//given
			Long concertId = 1L;
			Concert concert = defaultTestBuilder()
				.withStatus(ConcertStatus.BOOKING_OPEN)
				.buildConcert();
			given(concertReader.readNotDeleted(anyLong())).willReturn(concert);
			//when then
			assertThatThrownBy(() -> concertService.deleteConcert(concertId)).isInstanceOf(
				CannotDeleteConcertException.class).hasMessage("예매 전 상태의 공연만 삭제할 수 있습니다.");
		}

		@Test
		public void 콘서트_삭제_정상적으로_삭제_상태는_DELETE_이다() {
			//given
			Long concertId = 1L;
			Concert concert = defaultTestBuilder().buildConcert();
			given(concertReader.readNotDeleted(anyLong())).willReturn(concert);
			//when
			concertService.deleteConcert(concertId);
			//then
			assertThat(concert.getStatus()).isEqualByComparingTo(ConcertStatus.DELETED);
		}
	}

	private ConcertTestBuilder defaultTestBuilder() {
		return new ConcertTestBuilder()
			.withTitle("아이유콘서트")
			.withPerformer("아이유")
			.withDescription("단독 콘서트")
			.withConcertStartDateTime(LocalDateTime.of(2025, 3, 20, 0, 0))
			.withConcertEndDateTime(LocalDateTime.of(2025, 3, 24, 0, 0))
			.withBookingStartDateTime(LocalDateTime.of(2025, 3, 10, 0, 0))
			.withBookingEndDateTime(LocalDateTime.of(2025, 3, 20, 0, 0))
			.withLocation("올림픽 공원")
			.withStatus(ConcertStatus.PLANNED)
			.withId(1L);
	}

}