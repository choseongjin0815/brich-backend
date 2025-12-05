package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestApplicantVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestSearchCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseApplicantListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.exceptions.BrichException;

@RequestMapping("/api/v1/campaign")
@RestController
public class CampaignApi {
	private static final Logger log = LoggerFactory.getLogger(CampaignApi.class);
	
	@Autowired
	private CampaignService campaignService;

	// 석진 ===============================================================
	@GetMapping("list/{id}")
	public AjaxResponse readCampaignList(Authentication authentication,
										 @PathVariable String id,
										 RequestSearchCampaignVO requestSearchCampaignVO) {
		requestSearchCampaignVO.setListSize(8);
		requestSearchCampaignVO.setPageCountInGroup(5);
		requestSearchCampaignVO.setLoginId(id);
		
		
		ResponseCampaignListVO campaignList = this.campaignService.readCampaignListByUsrId(requestSearchCampaignVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(campaignList);
		ajaxResponse.setPaginator(requestSearchCampaignVO);
		
		log.info("campaigncheck : " + campaignList);
		return ajaxResponse;
	}
	
	@GetMapping("/applicant/{cmpnId}")
    public AjaxResponse readApplicantList(@PathVariable String cmpnId,
    									  RequestApplicantVO requestApplicantVO,
    									  Authentication authentication) {
    	requestApplicantVO.setListSize(10);
    	requestApplicantVO.setPageCountInGroup(10);
    	requestApplicantVO.setCmpnId(cmpnId);

    	if (requestApplicantVO.getOrder() != null) {
    		requestApplicantVO.setOrder(requestApplicantVO.getOrder().toUpperCase());
    	}
    	
    	ResponseApplicantListVO applicantList = this.campaignService.readApplicantListById(requestApplicantVO);
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(applicantList);
    	ajaxResponse.setPaginator(requestApplicantVO);
    	
    	log.info("applicant : ", applicantList);
    	return ajaxResponse;
    }
}
