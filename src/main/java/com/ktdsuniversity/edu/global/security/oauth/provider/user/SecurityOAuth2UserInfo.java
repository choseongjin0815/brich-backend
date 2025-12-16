package com.ktdsuniversity.edu.global.security.oauth.provider.user;

import java.util.Map;

public interface SecurityOAuth2UserInfo {

	/**
	 * OAuth 제공자(Naver, Google, ...)에서 전달해 주는 사용자의 정보 Set
	 * @return
	 */
	Map<String, Object> getAttributes();
	
	/**
	 * 사용자의 정보 Set에서 email만 추출/
	 * @return
	 */
	String getEmail();
	
	/**
	 * 사용자의 정보 Set에서 사용자 이름만 추출.
	 * @return
	 */
	String getName();
	
	String get(String key);
}
