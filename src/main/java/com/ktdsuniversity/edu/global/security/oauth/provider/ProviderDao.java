package com.ktdsuniversity.edu.global.security.oauth.provider;

import org.apache.ibatis.annotations.Mapper;

import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserRegistVO;
import com.ktdsuniversity.edu.global.security.oauth.provider.vo.OAuthProviderVO;



@Mapper
public interface ProviderDao {
	public int selectMemberCountByEmail(String email);
	
	public int insertOAuthMember(RequestUserRegistVO requestRegistMemberVO);
	
	public int insertOAuthProvider(OAuthProviderVO oAuthProviderVO);
	
	public int selectProviderCountByEmailAndProvider(OAuthProviderVO oAuthProviderVO);
}
