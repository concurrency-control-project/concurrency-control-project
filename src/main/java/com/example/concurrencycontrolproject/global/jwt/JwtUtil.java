package com.example.concurrencycontrolproject.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.concurrencycontrolproject.domain.auth.exception.InvalidTokenException;
import com.example.concurrencycontrolproject.domain.auth.exception.TokenNotFoundException;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.global.redis.RefreshCacheUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final RefreshCacheUtil redisCache;
	private final UserRepository userRepository;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
	//private static final long REFRESH_TOKEN_TIME = 60 * 60 * 24 * 1000L; // 1일
	private static final long REFRESH_TOKEN_TIME = 30 * 1000L;

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(Long userId, String email, UserRole userRole, String nickname) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("email", email)
				.claim("userRole", userRole)
				.claim("nickname", nickname)
				.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();
	}

	public String createRefreshToken(Long userId) {
		Date date = new Date();

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public void accessTokenSetHeader(String accessToken, HttpServletResponse response) {
		response.setHeader("Authorization", accessToken);
	}

	public void refreshTokenSetCookie(String token, HttpServletResponse response) {
		Cookie cookie = new Cookie("token", token);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		throw new TokenNotFoundException();
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public String validateExpiredAccessToken(String cookieJwt, String redisJwt) {
		Claims claims = extractClaims(cookieJwt);
		String userId = claims.getSubject();

		if (!redisJwt.equals(cookieJwt)) {
			throw new InvalidTokenException();
		}

		// 새로운 토큰 생성
		User auth = userRepository.findById(Long.parseLong(userId)).get();
		return createAccessToken(auth.getId(), auth.getEmail(), auth.getRole(), auth.getNickname());
	}

	public void reissueRefreshToken(String accessToken, HttpServletResponse response) {
		// 새로 생성하여 재발급
		String userId = extractClaims(accessToken).getSubject();
		String newRefreshToken = createRefreshToken(Long.valueOf(userId));

		redisCache.reissueRefreshToken(newRefreshToken, userId);
		refreshTokenSetCookie(newRefreshToken, response);
	}

	// token의 만료 여부 확인 -> 유효한 경우에만 false
	public boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			// 현재 시간과 토큰의 만료 시간 비교
			Date expirationDate = claims.getExpiration();
			return expirationDate != null && expirationDate.before(new Date());
		} catch (Exception e) {
			// 토큰 파싱 중 발생하는 모든 예외(만료, 서명 오류 등)를 true로 처리
			return true;
		}
	}
}
