-- 현재 락 키의 값을 가져옴
local currentValue = redis.call('GET', KEYS[1])
-- KEYS[1]: 락 키
-- ARGV[1]: 락 소유자 식별 값

-- 현재 락을 소유한 값과 해제를 요청한 값이 일치하는지 확인
if currentValue == ARGV[1] then
  -- 일치하면 락 삭제 => DEL 명령어
  return redis.call('DEL', KEYS[1]) -- 성공 시 1, 키가 없으면 0 반환
else
  -- 일치하지 않거나 키가 없으면 0 반환 => 해제 실패 or 이미 해제됨
  return 0
end