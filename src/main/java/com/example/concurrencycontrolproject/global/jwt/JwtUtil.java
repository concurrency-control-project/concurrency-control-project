package com.example.concurrencycontrolproject.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.concurrencycontrolproject.domain.auth.exception.InvalidTokenException;
import com.example.concurrencycontrolproject.domain.auth.exception.TokenNotFoundException;
import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

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

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
	private static final long REFRESH_TOKEN_TIME = 60 * 60 * 24 * 1000L; // 1일

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

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();
	}

	public void saveAccessToken(String accessToken, HttpServletResponse response) {
		response.setHeader("Authorization", accessToken);
	}

	public void saveRefreshToken(String refreshToken, String userId, HttpServletResponse response) {
		String token = substringToken(refreshToken);
		Cookie cookie = new Cookie("token", token);
		cookie.setPath("/");
		response.addCookie(cookie);

		RefreshToken jwt = new RefreshToken(userId, token);
		refreshTokenRepository.save(jwt);
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

		// 기존 토큰 삭제
		refreshTokenRepository.deleteById(userId);

		// 새로운 토큰 생성
		User auth = userRepository.findById(Long.parseLong(userId)).get();
		return createAccessToken(auth.getId(), auth.getEmail(), auth.getRole(), auth.getNickname());
	}

	public void reissueRefreshToken(String userId, HttpServletResponse response) {
		refreshTokenRepository.deleteById(userId);
		String newRefreshToken = createRefreshToken(Long.valueOf(userId));
		saveRefreshToken(newRefreshToken, userId, response);
	}

	public boolean isExpired(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
			.getBody().getExpiration().before(new Date());
	}
}
