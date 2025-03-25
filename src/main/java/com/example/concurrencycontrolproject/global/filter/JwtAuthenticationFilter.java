package com.example.concurrencycontrolproject.global.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.concurrencycontrolproject.domain.auth.exception.ExpiredJwtTokenException;
import com.example.concurrencycontrolproject.domain.auth.exception.InvalidJwtSignatureException;
import com.example.concurrencycontrolproject.domain.auth.exception.InvalidTokenException;
import com.example.concurrencycontrolproject.domain.auth.exception.TokenNotFoundException;
import com.example.concurrencycontrolproject.domain.auth.exception.UnsupportedJwtTokenException;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.global.jwt.JwtAuthenticationToken;
import com.example.concurrencycontrolproject.global.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException,
		IOException {
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String access = jwtUtil.substringToken(authorizationHeader);

			String refresh = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals("token"))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(TokenNotFoundException::new);

			try {
				Claims claims = jwtUtil.extractClaims(access);

				if (claims == null) {
					throw new InvalidTokenException();
				}

				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}

				Long id = Long.parseLong(claims.getSubject());
				request.setAttribute("userId", id);
				request.setAttribute("email", claims.get("email"));
				request.setAttribute("userRole", claims.get("userRole"));

				RefreshToken redis = refreshTokenRepository.findById(String.valueOf(id))
					.orElseThrow(ExpiredJwtTokenException::new);

				// jwt 토큰 만료 시간 검증
				expiredJwtToken(access, refresh, redis.getToken(), response);

			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
				throw new InvalidJwtSignatureException();
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
				throw new UnsupportedJwtTokenException();
			} catch (Exception e) {
				log.error("Internal server error", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(Claims claims) {
		Long suerId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("userRole", String.class));
		String nickname = claims.get("nickname", String.class);

		AuthUser authUser = new AuthUser(suerId, email, userRole, nickname);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private void expiredJwtToken(String accessToken, String refreshToken, String redisToken,
		HttpServletResponse response) {
		boolean isAccessTokenExpired = jwtUtil.isExpired(accessToken);
		boolean isRefreshTokenExpired = jwtUtil.isExpired(refreshToken);

		if (isAccessTokenExpired && isRefreshTokenExpired) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.");
			throw new ExpiredJwtTokenException();

			// access token 만료, refresh token 유효 -> redis에 저장된 토큰과 일치성 확인 후 재발급
		} else if (isAccessTokenExpired) {
			String newAccessToken = jwtUtil.validateExpiredAccessToken(refreshToken, redisToken);
			jwtUtil.saveAccessToken(newAccessToken, response);

			String userId = jwtUtil.extractClaims(newAccessToken).getSubject();
			jwtUtil.reissueRefreshToken(userId, response);

			// refresh token 만료, access token 유효 -> access 검증 후 refresh 재발급
		} else if (isRefreshTokenExpired) {
			String subject = jwtUtil.extractClaims(accessToken).getSubject();
			jwtUtil.reissueRefreshToken(subject, response);
		}
	}

}

