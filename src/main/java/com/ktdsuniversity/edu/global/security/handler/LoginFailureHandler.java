package com.ktdsuniversity.edu.global.security.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.ktdsuniversity.edu.domain.user.dao.UserDao;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFailureHandler implements AuthenticationFailureHandler{

	@Autowired
	private UserDao userDao;
	
	public LoginFailureHandler(UserDao userDao){
		this.userDao = userDao;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if(exception instanceof BadCredentialsException bce) {
			String email = bce.getMessage();
			
			this.userDao.updateLoginFailCountByLogId(email);
			this.userDao.updateBlockByLogid(email);
		}
		
		// /member/login 페이지가 보이도록 처리.
		// 사용자가 보내준 이메일 보내줌.
		// 에러 메시지도 보내줌.
		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/views/member/login.jsp");
		
		request.setAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다.");
		request.setAttribute("email", request.getParameter("email"));
		
		view.forward(request, response);
		
	}

}
