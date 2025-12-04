package com.ktdsuniversity.edu.global.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ktdsuniversity.edu.domain.user.dao.UserDao;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	private UserDao userDao;
	
	public LoginSuccessHandler(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 로그인에 성공한 회원의 아이디(이메일)
		UserVO authMember = (UserVO) authentication.getPrincipal();
		String logId = authMember.getLogId();
		// 로그인 기록
		this.userDao.updateLoginSuccessByLogId(logId);
		
		String nextUrl = request.getParameter("nextUrl");
		
		// "/list"로 이동
		response.sendRedirect("/list");
		
	}
}
