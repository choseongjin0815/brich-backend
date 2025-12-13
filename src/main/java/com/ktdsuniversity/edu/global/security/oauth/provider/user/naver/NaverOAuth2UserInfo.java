package com.ktdsuniversity.edu.global.security.oauth.provider.user.naver;

import java.util.Map;

import com.ktdsuniversity.edu.global.security.oauth.provider.user.SecurityOAuth2UserInfo;

public class NaverOAuth2UserInfo implements SecurityOAuth2UserInfo{

private Map<String, Object> attributes;
	
	public NaverOAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>) attributes.get("response");
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getEmail() {
		return this.attributes.get("email").toString();
	}

	@Override
	public String getName() {
		return this.attributes.get("name").toString();
	}

	@Override
	public String get(String key) {
		return this.attributes.get(key).toString();
	}

}
