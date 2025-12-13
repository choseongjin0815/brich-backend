package com.ktdsuniversity.edu.global.security.oauth.handler;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.security.jwt.JwtProvider;
import com.ktdsuniversity.edu.global.security.oauth.provider.user.authenticate.SecurityOAuthUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) 
        throws IOException {
        
        // SecurityOAuthUser에서 UserVO 바로 가져오기
        SecurityOAuthUser securityOAuthUser = (SecurityOAuthUser) authentication.getPrincipal();
        UserVO userVO = securityOAuthUser.getUserVO();
        
        // 기존 /auth와 동일하게 JWT 생성
        String jwt = jwtProvider.generate(Duration.ofDays(30), userVO);
        
        // React로 리다이렉트 (토큰 전달)
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + jwt;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}