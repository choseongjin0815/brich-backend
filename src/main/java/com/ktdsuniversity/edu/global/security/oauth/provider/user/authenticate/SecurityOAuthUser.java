package com.ktdsuniversity.edu.global.security.oauth.provider.user.authenticate;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.security.oauth.provider.user.SecurityOAuth2UserInfo;
import com.ktdsuniversity.edu.global.security.user.SecurityUser;


public class SecurityOAuthUser extends SecurityUser implements OAuth2User{

	private static final long serialVersionUID = 9159254750773917468L;
	
	private SecurityOAuth2UserInfo securityOAuth2UserInfo;
	
	public SecurityOAuthUser(UserVO userVO, SecurityOAuth2UserInfo securityOAuth2UserInfo) {
		super(userVO);
		this.securityOAuth2UserInfo = securityOAuth2UserInfo;
	}

	/**
	 * 
	 */
	
	@Override
	public Map<String, Object> getAttributes() {
		return this.securityOAuth2UserInfo.getAttributes();
	}

	@Override
	public String getName() {
		return this.securityOAuth2UserInfo.getName();
	}

	public String getEmail() {
		return this.securityOAuth2UserInfo.getEmail();
	}
}
