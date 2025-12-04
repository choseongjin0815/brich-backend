package com.ktdsuniversity.edu.global.security.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ktdsuniversity.edu.domain.user.vo.UserVO;

public class SecurityUser implements UserDetails {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9193625525427701038L;

	private UserVO userVO;
	
	public SecurityUser(UserVO userVO) {
		this.userVO = userVO;
	}
	
	public UserVO getUserVO() {
		return this.userVO;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.userVO.getRoles()
				.stream()
				.map(SimpleGrantedAuthority::new)
				.toList();
	}

	@Override
	public String getPassword() {
		return this.userVO.getPswrd();
	}

	@Override
	public String getUsername() {
		return this.userVO.getLogId();
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return this.userVO.getBlckYn().equals("N");
	}

}
