package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestApplicantVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestSearchCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseAdoptListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseApplicantListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseDenyHistoryVO;
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
	@GetMapping("list")
	public AjaxResponse readCampaignList(Authentication authentication,
										 RequestSearchCampaignVO requestSearchCampaignVO) {
		requestSearchCampaignVO.setListSize(8);
		requestSearchCampaignVO.setPageCountInGroup(5);
		
		
		ResponseCampaignListVO campaignList = this.campaignService.readCampaignListByUsrId(requestSearchCampaignVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(campaignList);
		ajaxResponse.setPaginator(requestSearchCampaignVO);
		
		log.info("campaigncheck : " + campaignList);
		return ajaxResponse;
	}
	
	@GetMapping("/applicant")
    public AjaxResponse readApplicantList(RequestApplicantVO requestApplicantVO,
    									  Authentication authentication) {
    	requestApplicantVO.setListSize(10);
    	requestApplicantVO.setPageCountInGroup(10);

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
	
	@PutMapping("/adoptChange")
    @ResponseBody
    public boolean doUpdateAdptYnAction(@RequestBody RequestApplicantVO requestApplicantVO,
    									Authentication authentication) {
//    	requestApplicantVO.setUsrId(loginUser.getUsrId());
    	boolean update = this.campaignService.updateAdptYnByCmpnPstAdptId(requestApplicantVO);
    	
    	if (update) {
    		return true;
    	}
    	
    	else {
    		return false;
    	}
    }
	
	@GetMapping("/adopt")
    public AjaxResponse readAdoptList(RequestApplicantVO requestApplicantVO,
    								  Authentication authentication) {
    	requestApplicantVO.setListSize(10);
    	requestApplicantVO.setPageCountInGroup(10);
    			
    	ResponseAdoptListVO adoptList = this.campaignService.readResponseAdoptListByCmpnId(requestApplicantVO);
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(adoptList);
    	ajaxResponse.setPaginator(requestApplicantVO);
    	
    	log.info("adoptList---:" + adoptList);
    	return ajaxResponse;
    }
	
	@GetMapping("/post-deny-history/{cmpnPstAdptId}")
	@ResponseBody
	public AjaxResponse doReadDenyHistoryAction(@PathVariable String cmpnPstAdptId) {
		List<ResponseDenyHistoryVO> history = this.campaignService.readDenyHistoryByCmpnPstAdptId(cmpnPstAdptId);

		AjaxResponse response = new AjaxResponse();
		response.setBody(history);
		
		return response;
	}
	
	@PutMapping("/post-approve/{cmpnPstAdptId}")
    @ResponseBody
    public boolean doUpdatePstSttsApproveAction(@PathVariable String cmpnPstAdptId,
    											Authentication authentication) {
    	RequestApplicantVO requestApplicantVO = new RequestApplicantVO();
    	requestApplicantVO.setCmpnPstAdptId(cmpnPstAdptId);
//    	requestApplicantVO.setUsrId(loginUser.getUsrId());	authentication.getPrincipal()로 가져올 것
    	requestApplicantVO.setUsrId("USR-20240413-000007");
    	boolean update = this.campaignService.updatePstSttsApproveByCmpnPstAdoptId(requestApplicantVO);
    	
    	if (update) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
