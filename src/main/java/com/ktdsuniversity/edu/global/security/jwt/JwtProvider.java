package com.ktdsuniversity.edu.global.security.jwt;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
	
	
	
	@Value("${app.jwt.issuer}")
	private String issuer;
	
	@Value("${app.jwt.secret-key}")
	private String secretKey;
	
	
	public String generate(Duration duration, UserVO userVO) {
		
		if(userVO == null){
			return null;
		}
		
		
		// 유효기간 설정.
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + duration.toMillis());
		
		// 암호화 키 생성.
		SecretKey jwtKey = Keys.hmacShaKeyFor(secretKey.getBytes());
		
		// JWT 생성
		String jwt = Jwts.builder()
						.issuer(issuer)
						.subject("brich")
						.claim("user", userVO)
						.issuedAt(now)
						.expiration(expireDate)
						.signWith(jwtKey)
						.compact();
						// 순서가 달라지면 무시될수있음
		// JWT 반환
		return jwt;
	}
	
	public UserVO verify(String jwt) throws JsonProcessingException {
		// JWT 복호화를 위한 키 설정
		SecretKey jwtKey = Keys.hmacShaKeyFor(secretKey.getBytes());
		
		// JWT를 복호화
		Object userClaim = Jwts.parser()
								.verifyWith(jwtKey) // 정해진 키롤 복호화가 실패한다면 토큰이 변조되었음을 의미. ==> Exception이 발생!
								.requireIssuer(this.issuer) // issuer가 ktds-university 아니라면 토큰이 변조되었음을 의미. ==> Exception 발생!
								.requireSubject("brich") // subject가 hello-spring이 아니라면 토큰이 변조되었음을 의미. ==> Exception 발생!
								.build() // 복호화 진행 시작.
								.parseSignedClaims(jwt)
								.getPayload() // Claim 추출.
								.get("user") // MemberVO 조회
 			;
		// claims (user) 추출. ==> JSON
		// 객체간의 복제 또는 변환을 담당하는 클래스.
		// Java Copy (Shallow, Deep) ==> Deep Copy 가능.
		ObjectMapper om = new ObjectMapper();
		String userJson = om.writeValueAsString(userClaim);
		// JSON ==> MemberVO로 변환.
		UserVO userVO = om.readValue(userJson, UserVO.class);
		// MemberVO 반환
		return userVO;
	}
}
