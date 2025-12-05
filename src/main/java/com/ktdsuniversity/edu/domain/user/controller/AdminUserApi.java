package com.ktdsuniversity.edu.domain.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.user.service.AdminUserService;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserBaseInfoVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserListVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

// @PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminUserApi {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminUserApi.class);
	
	@Autowired
	private AdminUserService adminUserService;

	/**
	 * 회원 관리 - 전체 회원 리스트
	 * @return
	 */
	@GetMapping("/users")
	public AjaxResponse getUsersList() {
		List<AdminUserListVO> usersList = this.adminUserService.readAdminAllUserList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(usersList);
		
		return ajaxResponse;
	}
	
	/**
	 * 회원 관리 - 블로거 리스트
	 * @return
	 */
	@GetMapping("/bloggers")
	public AjaxResponse getBloggersList() {
		List<AdminUserListVO> usersList = this.adminUserService.readAdminBloggerList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(usersList);
		
		return ajaxResponse;
	}
	
	/**
	 * 회원 관리 - 광고주 리스트
	 * @return
	 */
	@GetMapping("/advertisers")
	public AjaxResponse getAdvertisersList() {
		List<AdminUserListVO> usersList = this.adminUserService.readAdminAdvertiserList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(usersList);
		
		return ajaxResponse;
	}
	
	/**
	 * 회원 관리 - 회원 상세 정보
	 * @param usrId
	 * @return
	 */
	@GetMapping("/user-detail/{usrId}")
	public AjaxResponse getUserDetailById(@PathVariable String usrId) {
		AdminUserBaseInfoVO userInfo = this.adminUserService.readAdminUserDetailById(usrId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(userInfo);
		
		logger.info("user-detail: ", ajaxResponse);
		
		return ajaxResponse;
	}

}
