package com.ktdsuniversity.edu.domain.user.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ktdsuniversity.edu.domain.report.vo.AdminPenaltyRequestVO;
import com.ktdsuniversity.edu.domain.user.service.AdminUserService;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserBaseInfoVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserListVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserModifyInfoVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;

/**
 * 회원 관리
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminUserApi {
	
	private static final Logger log = LoggerFactory.getLogger(AdminUserApi.class);
	
	@Autowired
	private AdminUserService adminUserService;

	/**
	 * 전체 회원 리스트
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
	 * 블로거 리스트
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
	 * 광고주 리스트
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
	 * 회원 상세 정보
	 * @param usrId
	 * @return
	 */
	@GetMapping("/user-detail/{usrId}")
	public AjaxResponse getUserDetailById(@PathVariable String usrId) {
		AdminUserBaseInfoVO userInfo = this.adminUserService.readAdminUserDetailById(usrId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(userInfo);
		
		log.info("user-detail: ", ajaxResponse);
		
		return ajaxResponse;
	}
	
	/**
	 * 광고주 가입 승인
	 * @param usrId
	 * @param requestBody
	 * @return
	 */
	@PutMapping("/regist-approve/{usrId}")
	public AjaxResponse advertiserRegistApprove(@PathVariable String usrId, @RequestBody Map<String, String> requestBody) {
		boolean updateResult = this.adminUserService.updateAdvertiserRegistApprove(usrId, requestBody.get("adminId"));
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(updateResult);
		return ajaxResponse;
	}
	
	/**
	 * 광고주 가입 반려
	 * @param usrId
	 * @param requestBody
	 * @return
	 */
	@PutMapping("/regist-reject/{usrId}")
	public AjaxResponse advertiserRegistReject(@PathVariable String usrId, @RequestBody Map<String, String> requestBody) {
		boolean updateResult = this.adminUserService.updateAdvertiserRegistReject(usrId, requestBody.get("adminId"));
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(updateResult);
		return ajaxResponse;
	}
	
	/**
	 * 선택 가능한 블로그 전체 카테고리 받아오기
	 * @return
	 */
	@GetMapping("/blogcategory-list")
	public AjaxResponse getBlogCategoryList() {
		List<CommonCodeVO> blogCategoryList = this.adminUserService.readBlogCategoryList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(blogCategoryList);
		return ajaxResponse;
	}
	
	/**
	 * 회원 정보 수정
	 * @param adminUserModifyInfoVO
	 * @param newFiles
	 * @param fileToDeleteJson
	 * @return
	 */
	@PostMapping("/user-update")
	public AjaxResponse modifyUserInfo(@ModelAttribute AdminUserModifyInfoVO adminUserModifyInfoVO
									 , @RequestPart(name = "file", required = false) List<MultipartFile> newFiles
									 , @RequestPart(name = "fileToDelete", required = false) String fileToDeleteJson) {
		
		log.info("newFiles: {}", newFiles != null ? newFiles.size() : 0);
		log.info("fileToDelete: {}", fileToDeleteJson);
		
		boolean isSuccess = this.adminUserService.updateUserInfo(adminUserModifyInfoVO, newFiles, fileToDeleteJson);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);
		
		return ajaxResponse;
	}
	
	/**
	 * 블로거 블로그 주소 수동 인증
	 * @param adminUserModifyInfoVO
	 * @return 
	 */
	@PutMapping("/blog-address-certify/{usrId}")
	public AjaxResponse certifyBlogAddress(@RequestBody AdminUserModifyInfoVO adminUserModifyInfoVO) {
		boolean isSuccess = this.adminUserService.updateBlogAddress(adminUserModifyInfoVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);
		
		return ajaxResponse;
	}
	
	/**
	 * 회원 페널티 (경고/정지)
	 * @param adminPanaltyRequestVO
	 * @return
	 */
	@PostMapping("/user-panelty/{usrId}")
	public AjaxResponse userPenaltyProcess(@RequestBody AdminPenaltyRequestVO adminPanaltyRequestVO) {
		boolean isSuccess = this.adminUserService.updateUserPenaltyInfo(adminPanaltyRequestVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);
		
		return ajaxResponse;
	}

}
