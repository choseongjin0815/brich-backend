package com.ktdsuniversity.edu.global.security.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.security.jwt.JwtProvider;
import com.ktdsuniversity.edu.global.security.user.SecurityUser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtProvider jwtProvider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// JWT 인증 시작.
		// 모든 endpoint를 대상으로 인증.
		// request를 한 URL 확인.
		// /list ==> 인증 대상 X, /api/v1/list ==> 인증대상 O
		String requestURL = request.getServletPath();
		if (requestURL.startsWith("/api/")) {
			// RequestHeader에 있는 Authorization의 앖을 추출. ==> JWT
			String jwt = request.getHeader("Authorization");
			
//	       if (jwt == null || jwt.isBlank()
//	                || "null".equalsIgnoreCase(jwt)
//	                || "undefined".equalsIgnoreCase(jwt)) {
//	            filterChain.doFilter(request, response); // 그냥 다음 필터/컨트롤러로 넘김
//	            return;
//	        }
	       
			// JWT가 존재한다
			// JWT를 검증 / 복호화 ==> MemberVO
			if (jwt != null && jwt.length() > 0) {
				
				try {
				UserVO userVO = this.jwtProvider.verify(jwt);

				// MemberVO = SecurityContext에 등록.
				SecurityUser securityUser = new SecurityUser(userVO);

				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(userVO, null,
								securityUser.getAuthorities());
				
				SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				catch(MalformedJwtException | SignatureException mje) {
					AjaxResponse errorResponse = new AjaxResponse();
					errorResponse.setError(Map.of("message","토큰 변조가 감지되었습니다!"));
					
					Gson gson = new Gson();

					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json");
					
					PrintWriter out = response.getWriter();
					out.write(gson.toJson(errorResponse));
					out.flush();
					
					return;
				}
				catch(ExpiredJwtException eje) {
					AjaxResponse errorResponse = new AjaxResponse();
					errorResponse.setError(Map.of("message","인증이 만료되었습니다!"));
					
					Gson gson = new Gson();

					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json");
					
					PrintWriter out = response.getWriter();
					out.write(gson.toJson(errorResponse));
					out.flush();
					
					return;
				}
			}
		}

		// 위는 request 밑은 response
		filterChain.doFilter(request, response);

	}
		
	}


