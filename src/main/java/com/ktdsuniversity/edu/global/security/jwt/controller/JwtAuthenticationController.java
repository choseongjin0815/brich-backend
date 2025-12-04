package com.ktdsuniversity.edu.global.security.jwt.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.user.service.UserService;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserLoginVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.security.jwt.JwtProvider;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;


@RestController
public class JwtAuthenticationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@PostMapping("/auth")
	public AjaxResponse generateToken(@RequestBody RequestUserLoginVO requestUserLoginVO){
		
		UserVO userVO = this.userService.readUser(requestUserLoginVO);
		
		String jwt = this.jwtProvider.generate(Duration.ofDays(30), userVO);
		
		AjaxResponse jwtResponse = new AjaxResponse();
		if(jwt != null) {
			jwtResponse.setBody(jwt);
		}
		else {
			jwtResponse.setError(Map.of("message","아이디 또는 비밀번호가 일치하지 않습니다."));
		}
		
		
		return jwtResponse;
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/api/v1/account")
	public AjaxResponse getMyAccountInfo() {
		AjaxResponse response = new AjaxResponse();
		response.setBody(AuthenticationUtil.getUserVO());
		return response;
	}
	
}
