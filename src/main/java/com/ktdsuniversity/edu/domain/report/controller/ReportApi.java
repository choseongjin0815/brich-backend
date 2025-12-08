package com.ktdsuniversity.edu.domain.report.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.report.service.ReportService;
import com.ktdsuniversity.edu.domain.report.vo.ReportSearchVO;
import com.ktdsuniversity.edu.domain.report.vo.request.RequestReportCreateVO;
import com.ktdsuniversity.edu.domain.report.vo.response.ResponseMyReportInfoVO;
import com.ktdsuniversity.edu.domain.report.vo.response.ResponseReportDetailVO;
import com.ktdsuniversity.edu.domain.report.vo.response.ResponseReportVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

@RestController
@RequestMapping("/api/v1/report")
public class ReportApi {

	@Autowired
	private ReportService reportService;

	// 신고글 리스트
	// TODO pathVariable제거
	@GetMapping
	public AjaxResponse getReportList(ReportSearchVO reportSearchVO) {
		AjaxResponse ajaxResponse = new AjaxResponse();

		// 신고 목록 조회 (페이징)
		List<ResponseMyReportInfoVO> reportList = this.reportService.readMyReportListWithPaging(reportSearchVO);

		ajaxResponse.setBody(Map.of("reportList", reportList, "reportSearch", reportSearchVO));

		return ajaxResponse;
	}

	// 신고글 작성
	@PostMapping
	public AjaxResponse createReport(RequestReportCreateVO requestReportCreateVO) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		boolean reportResult = this.reportService.createNewReport(requestReportCreateVO);

		if (reportResult) {
			ajaxResponse.setBody("신고가 성공적으로 접수되었습니다!");
		} else {
			ajaxResponse.setBody("신고 접수에 실패했습니다.");
		}
		return ajaxResponse;
	}

	// 신고글 작성 페이지 기본 정보
	@GetMapping("/{targetUsrId}")
	public AjaxResponse getBaseReportInfo(@PathVariable String targetUsrId) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		ResponseReportVO report = this.reportService.readTargetInfoByTargetUsrId(targetUsrId);

		if (report != null) {
			ajaxResponse.setBody(report);
		}

		return ajaxResponse;
	}

	// 신고글 상세보기
	@GetMapping("/view/{reportId}")
	public AjaxResponse getReportDetail(@PathVariable String reportId) {
		AjaxResponse ajaxResponse = new AjaxResponse();

		ResponseReportDetailVO reportDetail = this.reportService.readReportDetailByReportId(reportId);
		if (reportDetail != null)
			ajaxResponse.setBody(reportDetail);

		return ajaxResponse;
	}

}
