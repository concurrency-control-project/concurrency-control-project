local result = redis.call('SET', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2])
-- KEYS[1]: 락 키 (예: "lock:scheduleIdSeatId:1-100")
-- ARGV[1]: 락 소유자 식별 값 (예: UUID 또는 스레드 ID)
-- NX: 키가 존재하지 않을 때만 설정
-- PX: 만료 시간을 밀리초 단위로 설정
-- ARGV[2]: 락 만료 시간 (밀리초 단위, TTL)

if result == 'OK' then
  return 1
else
  return 0
end