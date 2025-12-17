package com.ktdsuniversity.edu.global.interceptors;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.servlet.HandlerInterceptor;

import com.ktdsuniversity.edu.domain.user.vo.UserVO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckSessionInterceptor implements HandlerInterceptor{
	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		
//		String[] canLoginList = { "1001", "1002", "1003", "1004" };
//		
//		HttpSession httpSession = request.getSession();
//		UserVO loginUser = (UserVO) httpSession.getAttribute("__LOGIN_USER__");
//			
//		if(loginUser == null) {
//			//로그인 페이지 보여주기
//			//1. view 찾기임
//			RequestDispatcher view = 
//					request.getRequestDispatcher("/WEB-INF/views/user/login.jsp"); //세션이 없으면 뭐든지 간에 얘를 보여주게 해
//			//2. view 보여주기 
//			view.forward(request, response);
//			
//			//Session이 없으면 컨트롤러를 실행하지 않는다.
//			return false;
//		}
//		
//		return true;
//	}
//	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

	    String uri = request.getRequestURI();
	    String method = request.getMethod();

	    // 1) 프리플라이트는 무조건 통과 (중요)
	    if ("OPTIONS".equalsIgnoreCase(method)) return true;

	    // 2) ✅ API / 결제 엔드포인트는 세션 로그인 체크 제외 (중요)
	    if (uri.startsWith("/api/")
	            || uri.equals("/orders/prepay")
	            || uri.equals("/confirm")
	            || uri.startsWith("/oauth2/")) {
	        return true;
	    }

	    // 3) ✅ 세션이 없으면 "만들지 말고" 조회 (중요: getSession() 쓰면 새 세션 생성됨)
	    HttpSession httpSession = request.getSession(false);
	    UserVO loginUser = (httpSession == null) ? null : (UserVO) httpSession.getAttribute("__LOGIN_USER__");

	    if (loginUser == null) {
	        RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/views/user/login.jsp");
	        view.forward(request, response);
	        return false;
	    }

	    return true;
	}

}
