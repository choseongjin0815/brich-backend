package com.ktdsuniversity.edu.global.security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ktdsuniversity.edu.global.util.SHAEncrypter;

@Component
public class SecurityPasswordEncoder implements PasswordEncoder{

	
	public String encode(String rawPassword, String salt) {
		return SHAEncrypter.getEncrypt(rawPassword, salt);
	}
	
	
	@Override
	public String encode(CharSequence rawPassword) {
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.equals(encodedPassword);
	}

}
