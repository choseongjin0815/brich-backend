package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.campaign.service.AdminCampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestAdminCamapaignRejectVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestAdminCampaignApplicantVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestAdminCampaignApproveVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestAdminSearchCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseAdminCampaignApplicantListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseAdminCampaignListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseAdminCampaignVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

/**
 * 캠페인 관리
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminCampaignApi {
	
	private static final Logger log = LoggerFactory.getLogger(AdminCampaignApi.class);

	@Autowired
	private AdminCampaignService adminCampaignService;

	/**
	 * 캠페인 목록 조회
	 * @param requestAdminSearchCampaignVO
	 * @return
	 */
	@PostMapping("/campaign-list")
	public AjaxResponse getCampaignList(@RequestBody RequestAdminSearchCampaignVO requestAdminSearchCampaignVO) {
		
		ResponseAdminCampaignListVO campaignListAndCategory = this.adminCampaignService.readAdminCampaignListAndCategory(requestAdminSearchCampaignVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(campaignListAndCategory);
    	
    	return ajaxResponse;
		
	}
	
	/**
	 * 캠페인 상세 조회
	 * @param cmpnId
	 * @return
	 */
	@GetMapping("/campaign-detail/{cmpnId}")
	public AjaxResponse getCampaignDetail(@PathVariable String cmpnId) {
		
		ResponseAdminCampaignVO detail = this.adminCampaignService.readAdminCampaignDetail(cmpnId);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(detail);
    	
    	return ajaxResponse;
	}
	
	/**
	 * 캠페인 승인
	 * @param cmpnId
	 * @param approveInfo
	 * @return
	 */
	@PutMapping("/campaign-approve/{cmpnId}")
	public AjaxResponse campaignApproveProcess(@PathVariable String cmpnId, 
											@RequestBody RequestAdminCampaignApproveVO approveInfo) {
		boolean updateResult = this.adminCampaignService.updateAdminCampaignApprove(approveInfo);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(updateResult);
		return ajaxResponse;
	}
	
	/**
	 * 캠페인 반려
	 * @param rejectInfo
	 * @return
	 */
	@PutMapping("/campaign-reject/{cmpnId}")
	public AjaxResponse campaignRejectProcess(@PathVariable String cmpnId, 
											@RequestBody RequestAdminCamapaignRejectVO rejectInfo) {
		
		boolean isSuccess = this.adminCampaignService.updateAdminCampaignReject(rejectInfo);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);
		
		return ajaxResponse;
	}
	
	/**
	 * 캠페인 신청자 탭
	 * @param cmpnId
	 * @param requestAdminApplicantVO
	 * @return
	 */
	@GetMapping("/campaign-applicant")
	public AjaxResponse getCampaignApplicant(RequestAdminCampaignApplicantVO requestAdminApplicantVO) {
		requestAdminApplicantVO.setListSize(10);
		requestAdminApplicantVO.setPageCountInGroup(10);
		requestAdminApplicantVO.setCmpnId(requestAdminApplicantVO.getCmpnId());
    	
    	if (requestAdminApplicantVO.getOrder() != null) {
    		requestAdminApplicantVO.setOrder(requestAdminApplicantVO.getOrder().toUpperCase());
    	}
    	
    	ResponseAdminCampaignApplicantListVO applicantListVO = this.adminCampaignService.readAdminCampaignApplicantListById(requestAdminApplicantVO);
		
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(applicantListVO);
    	ajaxResponse.setPaginator(requestAdminApplicantVO);
    	
    	log.info("applicantListVO : ", applicantListVO);
    	
    	return ajaxResponse;
	}
	
}
