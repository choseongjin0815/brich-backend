package com.ktdsuniversity.edu.domain.report.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.report.service.AdminReportService;
import com.ktdsuniversity.edu.domain.report.vo.AdminPenaltyRequestVO;
import com.ktdsuniversity.edu.domain.report.vo.AdminReportDetailVO;
import com.ktdsuniversity.edu.domain.report.vo.AdminReportListVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminReportApi {
	
	private static final Logger log = LoggerFactory.getLogger(AdminReportApi.class);
	
	@Autowired
	private AdminReportService adminReportService;
	
	/**
	 * 신고 목록
	 * @return
	 */
	@GetMapping("/report-list")
	public AjaxResponse getReportList() {
		List<AdminReportListVO> reportList = this.adminReportService.readAdminReportList();
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(reportList);
		
		return ajaxResponse;
	}
	
	/**
	 * 신고 상세
	 * @param rptId
	 * @return
	 */
	@GetMapping("/report-detail/{rptId}")
	public AjaxResponse getReportDetail(@PathVariable String rptId) {
		AdminReportDetailVO reportInfo = this.adminReportService.readAdminReportDetailById(rptId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(reportInfo);
		
		return ajaxResponse;
	}
	
	/**
	 * 신고 처리 (기각/경고/정지)
	 * @param requestVO
	 * @return
	 */
	@PostMapping("/report-penalty/{rptId}")
	public AjaxResponse reportPenaltyProcess(@RequestBody AdminPenaltyRequestVO requestVO) {
		boolean isSuccess = this.adminReportService.updateReportAndPenaltyInfo(requestVO);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);
		
		return ajaxResponse;
	}

}
