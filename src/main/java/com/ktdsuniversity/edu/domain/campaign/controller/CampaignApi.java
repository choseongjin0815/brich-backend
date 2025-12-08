package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.blog.service.BlogDataService;
import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestSearchCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.exceptions.BrichException;

@RequestMapping("/api/v1/campaign")
@RestController
public class CampaignApi {
	private static final Logger log = LoggerFactory.getLogger(CampaignApi.class);
	
	@Autowired
	private CampaignService campaignService;
	@Autowired
    private BlogDataService blogDataService;

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
		ajaxResponse.setNowPage(requestSearchCampaignVO.getPageNo());
		ajaxResponse.setDone(requestSearchCampaignVO.getPageNo() == requestSearchCampaignVO.getPageCount() - 1);
		
		log.info("campaigncheck : " + campaignList);
		return ajaxResponse;
	}
	
	
	// Hapa ===============================================================
    
    @PostMapping("/main")
    public AjaxResponse campaignMainPage(@RequestBody RequestSearchCampaignVO requestSearchCampaignVO){

    	log.info( "입력 파라미터 값 : " + requestSearchCampaignVO.toString());
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readCampaignListAndCategory(requestSearchCampaignVO);
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory);
    	
    	
    	log.info( "캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	return ajaxResponse;
    }  
    
    @GetMapping("/detail/{campaignId}")
    public AjaxResponse campaignDetailPage(@PathVariable String campaignId) {
    	ResponseCampaignVO detail = new ResponseCampaignVO();
    	
    	//지울부분
    	UserVO loginUser = new UserVO();
    	loginUser.setAutr("1002");
    	loginUser.setUsrId("USR-20251110-000271");
    	
    	if(loginUser != null) {
    		if(loginUser.getAutr().equals("1002") || loginUser.getAutr().equals("1003") ) {
        		detail = campaignService.readCampaignDetail(campaignId, loginUser.getUsrId());    	
        		String returnReason =  campaignService.postReturnReason(campaignId, loginUser.getUsrId());
        	}else {    		
        		detail = campaignService.readCampaignDetail(campaignId);
        	}
    	}else {    		
    		detail = campaignService.readCampaignDetail(campaignId);
    	}
        String usrId = (loginUser != null) ? loginUser.getUsrId() : null;
        

        // 블로그 지수 (BlogDataService에서 직접)
        Double myIndex = (usrId != null) ? blogDataService.readRecentBlogIndex(usrId) : 0.0;

        // 캠페인 참여자 통계
        Map<String, Object> stats = campaignService.readCampaignIndexStats(campaignId, usrId);
        detail.setStats(stats);
        
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(detail);
    	
    	log.info( "캠페인 상세조회 결과 : " + detail.toString());
    	return ajaxResponse;
    }
}
