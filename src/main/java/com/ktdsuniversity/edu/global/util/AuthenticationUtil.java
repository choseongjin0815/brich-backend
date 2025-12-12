package com.ktdsuniversity.edu.global.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.ktdsuniversity.edu.domain.user.vo.UserVO;

public class AuthenticationUtil {
	public static String getEmail() {
		UserVO authMember = (UserVO)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		return authMember.getLogId();
	}
	
	public static UserVO getUserVO() {
		Object authMember =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(authMember instanceof UserVO ) {
			return (UserVO)authMember;
		} else  {
			return  null;
		}
		
	}

//	public static UserVO getUserVO() {
//		UserVO authMember = (UserVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			
//		return authMember;
//	}
}


