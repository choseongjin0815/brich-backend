package com.ktdsuniversity.edu.global.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ktdsuniversity.edu.global.security.service.SecurityPasswordEncoder;
import com.ktdsuniversity.edu.global.security.user.SecurityUser;

@Component
public class SecurityAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String logId = authentication.getName();
		
		String password = authentication.getCredentials().toString();
		
		SecurityUser securityUser = (SecurityUser) this.userDetailsService.loadUserByUsername(logId);
		
		if(!securityUser.isAccountNonLocked()) {
			throw new LockedException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		
		String encryptedPassword = ((SecurityPasswordEncoder)(this.passwordEncoder)).encode(password,securityUser.getUserVO().getSalt());
		
		boolean matches = this.passwordEncoder.matches(encryptedPassword, securityUser.getPassword());
		
		if(matches) {
			return new UsernamePasswordAuthenticationToken(securityUser.getUserVO(), encryptedPassword, securityUser.getAuthorities());
		}
		
		throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	
}
