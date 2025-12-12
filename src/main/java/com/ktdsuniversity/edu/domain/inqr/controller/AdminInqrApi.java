package com.ktdsuniversity.edu.domain.inqr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.inqr.service.AdminInqrService;
import com.ktdsuniversity.edu.domain.inqr.vo.AdminInqrDetailVO;
import com.ktdsuniversity.edu.domain.inqr.vo.AdminInqrListVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

/**
 * 문의 관리
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminInqrApi {
	
private static final Logger log = LoggerFactory.getLogger(AdminInqrApi.class);
	
	@Autowired
	private AdminInqrService adminInqrService;

	/**
	 * 문의 리스트
	 * @return
	 */
	@GetMapping("/inqr-list")
	public AjaxResponse getInqrList() {
		List<AdminInqrListVO> usersList = this.adminInqrService.readAdminInqrList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(usersList);
		
		return ajaxResponse;
	}
	
	/**
	 * 문의 상세
	 * @param inqrId
	 * @return
	 */
	@GetMapping("/inqr-detail/{inqrId}")
	public AjaxResponse getInqrDetail(@PathVariable String inqrId) {
		AdminInqrDetailVO inqrInfo = this.adminInqrService.readAdminInqrDetailByInqrId(inqrId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(inqrInfo);
		
		return ajaxResponse;
	}

}
