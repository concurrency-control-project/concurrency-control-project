package com.example.concurrencycontrolproject.domain.Ticket.DistributedLock;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.concurrencycontrolproject.domain.Ticket.service.TicketServiceTest;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.domain.userTicket.repository.UserTicketRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
public class TicketDistributedLockTest {

	@Autowired
	private TicketService ticketService;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserTicketRepository userTicketRepository;

	@Autowired
	private ConcertRepository concertRepository;

	Logger logger = LoggerFactory.getLogger(TicketServiceTest.class);

	private Seat seat1;
	private Schedule schedule1;
	private final int numberOfThreads = 5; // 동시에 시도할 스레드 개수 = 5
	private List<AuthUser> authUsers;

	@BeforeEach
	void setUp() {
		seat1 = Seat.of(1, "A석", 100000, "A열");
		seatRepository.save(seat1);

		Concert concert = concertRepository.save(
			Concert.builder()
				.title("Spring Festival")
				.performer("Artist")
				.description("Awesome spring concert")
				.concertStartDateTime(LocalDateTime.of(2025, 5, 1, 18, 0))
				.concertEndDateTime(LocalDateTime.of(2025, 5, 31, 22, 0))
				.bookingStartDateTime(LocalDateTime.of(2025, 4, 1, 0, 0))
				.bookingEndDateTime(LocalDateTime.of(2025, 4, 30, 23, 59))
				.location("Seoul")
				.build()
		);
		schedule1 = scheduleRepository.save(
			Schedule.of(
				concert,
				LocalDateTime.of(2025, 5, 22, 20, 0),
				ScheduleStatus.ACTIVE));

		// 각 스레드에 할당할 사용자 생성 => 여러 사람이 동시에 예매한 것 처럼 테스트
		authUsers = new ArrayList<>();

		for (int i = 0; i < numberOfThreads; i++) {
			User newUser = new User("user" + i + "@email.com", "user 비밀번호 " + i, "분산락 테스트 user " + i, "0100000000" + i);
			userRepository.save(newUser);
			authUsers.add(
				new AuthUser(newUser.getId(), "user" + i + "@email.com", UserRole.ROLE_USER, "분산락 테스트 authUsers " + i));
		}

		// entityManager.flush(); // @Transactional 없어서 오류 발생
		entityManager.clear();
	}

	@AfterEach
	void tearDown() { // @Transactional 롤백이 없으므로 수동 데이터 삭제 필요
		// 순서 중요 => 외래 키 제약 조건을 고려하여 자식 테이블부터 삭제
		userTicketRepository.deleteAll(); // UserTicket 먼저 삭제
		ticketRepository.deleteAll(); // Ticket 삭제

		// setUp 에서 생성한 Schedule + Seat 삭제

		if (schedule1 != null && schedule1.getId() != null) {
			scheduleRepository.deleteById(schedule1.getId());
		}

		if (seat1 != null && seat1.getId() != null) {
			seatRepository.deleteById(seat1.getId());
		}
	}

	@Test
	@DisplayName("동시에 여러 사용자가 같은 좌석 예매 시도 시 한 명만 성공")
	void saveTicket_concurrencyTest() throws InterruptedException { // 분산락 테스트
		// given
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads); // 입력된 수의 스레드를 관리하는 풀

		CountDownLatch latch = new CountDownLatch(numberOfThreads); // 모든 스레드가 준비될 때까지 대기 => 동시 시작 유도
		CountDownLatch finishLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 끝날 때까지 대기

		AtomicInteger successCount = new AtomicInteger(0); // 성공 카운트
		AtomicInteger failCount = new AtomicInteger(0); // 실패 카운트

		// 모든 스레드가 동시에 예매를 시도할 대상 스케줄 ID, 시트 ID
		Long scheduleId = schedule1.getId();
		Long seatId = seat1.getId();

		// when
		// 정의된 스레드 수만큼 반복하면서 각 스레드가 수행할 작업을 정의 => 스레드 풀에 제출
		for (int i = 0; i < numberOfThreads; i++) {
			final int userIndex = i; // 유저 인덱스

			// 스레드 풀에 수행할 작업 제출
			executorService.submit(() -> {
				try {
					latch.countDown(); // 준비 완료 카운트다운
					latch.await(); // 모든 스레드가 준비될 때까지 대기

					// 락 테스트 메서드
					ticketService.saveTicket(authUsers.get(userIndex), scheduleId, seatId);
					successCount.incrementAndGet(); // 성공 시 카운트 증가

					// saveTicket 예외 처리
					// Ticket 관련 예외
				} catch (TicketException e) {
					if (e.getErrorCode() == TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST) {
						failCount.incrementAndGet(); // 예상된 실패 카운트 증가
					} else {
						// 에러 로깅 시, 해당 스레드 정보도 포함하는 게 좋음
						logger.error("[Thread-{}] 예상치 못한 TicketException 발생: {}",
							Thread.currentThread().getId(), e.getMessage(), e);
					}

					// 락 획득 실패 관련 예외
				} catch (RuntimeException e) {
					if (e.getMessage() != null && e.getMessage().contains("획득 실패한 락")) {
						failCount.incrementAndGet(); // 락 획득 실패 카운트

						// DB Unique 조건 위반으로 실패한 경우 => DataIntegrityViolationException
					} else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
						Throwable rootCause = e;

						while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
							rootCause = rootCause.getCause();
						}

						// 원인 예외가 SQLIntegrityConstraintViolationException 이고,
						// 메시지에 조건 이름 UK_ticket_schedule_seat 포함 확인
						if (rootCause instanceof java.sql.SQLIntegrityConstraintViolationException &&
							rootCause.getMessage() != null && rootCause.getMessage()
							.contains("UK_ticket_schedule_seat")) {
							failCount.incrementAndGet(); // DB 유나크 조건 위반도 예상된 실패로 카운트
							logger.info("[Thread-{}] 예상된 DB 유니크 조건 위반 발생: {}", Thread.currentThread().getId(),
								rootCause.getMessage());
						} else {
							logger.error("[Thread-{}] 예상치 못한 RuntimeException 발생: {}",
								Thread.currentThread().getId(), e.getMessage(), e);
						}
					}

					// 스레드 중단 관련 예외
				} catch (InterruptedException e) {
					logger.error("[Thread-{}] 스레드 중단됨", Thread.currentThread().getId(), e);
					Thread.currentThread().interrupt();

				} finally { // finally 로 성공하든 실패하든 최종적으로 finishLatch 에 카운트다운 넣음
					finishLatch.countDown(); // 스레드 작업 완료 알림
				}
			});
		}

		finishLatch.await(); // 모든 스레드가 끝날 때까지 대기
		executorService.shutdown(); // 모든 작업 완료 후 스레드 풀 종료

		// then
		assertThat(successCount.get()).isEqualTo(1); // 정확히 하나의 스레드만 성공해야 함
		assertThat(failCount.get()).isEqualTo(numberOfThreads - 1); // 나머지는 실패해야 함

		// DB 확인 => 1개만 성공 했으니 1개의 티켓만 존재해야함
		long reservedTicketCount = ticketRepository.countByScheduleIdAndSeatIdAndStatus(scheduleId, seatId,
			TicketStatus.RESERVED);
		assertThat(reservedTicketCount).isEqualTo(1);

	}
}
