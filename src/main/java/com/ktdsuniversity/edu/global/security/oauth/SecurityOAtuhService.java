package com.ktdsuniversity.edu.global.security.oauth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.domain.user.dao.UserDao;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserRegistVO;
import com.ktdsuniversity.edu.global.security.oauth.provider.ProviderDao;
import com.ktdsuniversity.edu.global.security.oauth.provider.user.SecurityOAuth2UserInfo;
import com.ktdsuniversity.edu.global.security.oauth.provider.user.authenticate.SecurityOAuthUser;
import com.ktdsuniversity.edu.global.security.oauth.provider.user.naver.NaverOAuth2UserInfo;
import com.ktdsuniversity.edu.global.security.oauth.provider.vo.OAuthProviderVO;

@Service
public class SecurityOAtuhService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
	@Autowired
	private ProviderDao providerDao;
	
	@Autowired
	private UserDao userDao;
	
	// OAuth 요청
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		// 기본 OAuth 인증 서비스 생성 및 호출.
		OAuth2UserService<OAuth2UserRequest,OAuth2User> defaultService = new DefaultOAuth2UserService();
		
		OAuth2User oAuth2User = defaultService.loadUser(userRequest);
		
		// 사용자가 어떤 OAuth 서비스를 이용했는가?
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		
		// 사용자가 Naver OAuth로 로그인을 했다면 
		// Naver를 이용하여
		SecurityOAuth2UserInfo oAuthUserInfo = null;
		if(registrationId.equals("naver")) {
			oAuthUserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
		}
//		else if(registrationId.equals("google")){
//			oAuthUserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
//		}
		
		
		// 회원정보 생성/
		int count = this.providerDao.selectMemberCountByEmail(oAuthUserInfo.getEmail());
		// 존재하지 않는 이메일이었다!
		if(count == 0) {
			// 회원 가입 진행!
			RequestUserRegistVO registUserVO = new RequestUserRegistVO();
			registUserVO.setEml(oAuthUserInfo.getEmail());
			registUserVO.setNm(oAuthUserInfo.getName());
			registUserVO.setSalt("-");
			registUserVO.setPswrd("-");
			registUserVO.setLogId("-");
			registUserVO.setAutr("-");
			int providerCount = this.providerDao.insertOAuthMember(registUserVO);

			// Provider 등록!
			OAuthProviderVO oAuthProviderVO = new OAuthProviderVO();
			oAuthProviderVO.setEmail(oAuthUserInfo.getEmail());
			oAuthProviderVO.setProvider(registrationId);
			this.providerDao.insertOAuthProvider(oAuthProviderVO);
			
		}
		//존재하는 이메일이었다!
		else {
			// Provider가 존재하는가?
			OAuthProviderVO oAuthProviderVO = new OAuthProviderVO();
			oAuthProviderVO.setEmail(oAuthUserInfo.getEmail());
			oAuthProviderVO.setProvider(registrationId);
			int providerCount = this.providerDao.selectProviderCountByEmailAndProvider(oAuthProviderVO);
			// 없는 provider이었다!
			if(providerCount == 0) {
				// provider만 등록
				this.providerDao.insertOAuthProvider(oAuthProviderVO);
			}
//			String nickname = oAuthUserInfo.get("nickname");
//			String gender = oAuthUserInfo.get("gender");
			// TODO DB Insert
			// TODO 인증 정보에 추가
		}
		// 존재하는 provider이었다!
		// 회원 정보를 조회해서 인증 정보 생성.
		UserVO userVO = this.userDao.selectUserEmail(oAuthUserInfo.getEmail());
		List<String> roles = this.userDao.selectRolesByEmail(oAuthUserInfo.getEmail());
		userVO.setRoles(roles);
		return new SecurityOAuthUser(userVO,oAuthUserInfo);
	}

}
