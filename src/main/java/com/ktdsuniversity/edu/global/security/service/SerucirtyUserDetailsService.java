package com.ktdsuniversity.edu.global.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ktdsuniversity.edu.domain.user.dao.UserDao;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.security.user.SecurityUser;

@Component
public class SerucirtyUserDetailsService implements UserDetailsService{

	@Autowired
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVO userVO = this.userDao.selectUserByLogId(username);
		if(userVO == null) {
			throw new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		if(userVO.getBlckYn().equals("Y")) {
			int count = this.userDao.selectUnblockUserByLogId(username);
			if(count>0) {
				userVO.setBlckYn("N");
			}
		}
		
		
		// 권한 조회.
		List<String> roles = this.userDao.selectRolesByLogId(username);
		userVO.setRoles(roles);
		
		
		return new SecurityUser(userVO);
	}

}
