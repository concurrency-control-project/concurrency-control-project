package com.example.concurrencycontrolproject.global.filter;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.concurrencycontrolproject.domain.auth.exception.AuthException;
import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;
import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (AuthException e) {
			log.error("Unexpected error occurred", e);
			setErrorResponse(response, e.getErrorCode());
		}
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage());
		String jsonError = objectMapper.writeValueAsString(errorResponse);

		PrintWriter writer = response.getWriter();
		writer.write(jsonError);
		writer.flush();

		log.error("Error Response: {}", jsonError);
	}
}
