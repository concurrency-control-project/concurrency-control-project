package com.example.concurrencycontrolproject.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.concurrencycontrolproject.domain.common.dto.AuthUser;
import com.example.concurrencycontrolproject.domain.token.entity.RefreshToken;
import com.example.concurrencycontrolproject.domain.token.repository.RefreshTokenRepository;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
			String jwt = jwtUtil.substringToken(authorizationHeader);

			try {
				Claims claims = jwtUtil.extractClaims(jwt);

				if (claims == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
					return;
				}

				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}

				Long id = Long.parseLong(claims.getSubject());
				request.setAttribute("userId", id);
				request.setAttribute("email", claims.get("email"));
				request.setAttribute("userRole", claims.get("userRole"));

				Optional<Cookie> token = Arrays.stream(request.getCookies())
					.filter(cookie -> cookie.getName().equals("token"))
					.findFirst()
					.orElseThrow(re);
				Optional<RefreshToken> byId = refreshTokenRepository.findById(String.valueOf(id));

			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
			} catch (ExpiredJwtException e) {
				log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
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
}

