package com.ktdsuniversity.edu.domain.blog.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktdsuniversity.edu.domain.blog.service.BlogDataService;
import com.ktdsuniversity.edu.domain.blog.vo.BlogDetailStatVO;
import com.ktdsuniversity.edu.domain.blog.vo.BlogIndexVO;
import com.ktdsuniversity.edu.domain.blog.vo.DailyVisitorVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestBlogIndexListVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestExpireSoonCampaignVO;
import com.ktdsuniversity.edu.domain.blog.vo.ResponseDashboardVO;
import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.CampaignPostManageVO;
import com.ktdsuniversity.edu.domain.campaign.vo.CampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.ResponseExpireSoonListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseDenyHistoryVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.exceptions.AjaxException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogApi {
	private static final Logger log = LoggerFactory.getLogger(BlogDataController.class);

	@Autowired BlogDataService blogDataService;
	@Autowired CampaignService campaignService;

	@PreAuthorize("isAuthenticated() or hasRole('ROLE_BLG')" )
	@GetMapping("/dashboard/{usrId}")
	public AjaxResponse viewBlogDashBoard(@PathVariable String usrId,
										  RequestExpireSoonCampaignVO requestExpireSoonCampaignVO, 
										  Authentication authentication,
										  RequestBlogIndexListVO requestBlogIndexListVO) 
												  throws JsonProcessingException {
	    
	    if (authentication == null) {
	        throw new AjaxException(null, HttpStatus.BAD_REQUEST);
	    }
	    //UserVO loginUser
		requestExpireSoonCampaignVO.setListSize(15);
		requestExpireSoonCampaignVO.setPageCount(1);

		ResponseExpireSoonListVO result =
				this.blogDataService.readExpireSoonCampaignList(requestExpireSoonCampaignVO);
		List<BlogIndexVO> indexResult = 
				this.blogDataService.readBlogIndexList(usrId);

		List<DailyVisitorVO> dailyVisitorsResult =
				this.blogDataService.selectDailyVisitors(usrId);
		List<CommonCodeVO> goldenKeywordList =
				this.blogDataService.selectUserCategoryKeywords(usrId);
		List<CampaignVO> recommendResult = this.blogDataService.selectRecommendCampaign(usrId);
		ResponseCampaignListVO CampaignListAndCategory = 
				campaignService.readSubmittedMyCampaignByBlgId(usrId);
		double currentIndex = this.blogDataService.selectMostRecentIndex(usrId);
		int totalVisitor = this.blogDataService.selectTotalVisitor(usrId);

		ResponseDashboardVO responseDashboardVO = new ResponseDashboardVO();
		responseDashboardVO.setBlogIndexList(indexResult);
		responseDashboardVO.setCurrentIndex(currentIndex);
		responseDashboardVO.setDailyVisitorList(dailyVisitorsResult);
		responseDashboardVO.setGoldenKeywordList(goldenKeywordList);
		responseDashboardVO.setRecommendedCampaignList(recommendResult);
		responseDashboardVO.setResponseCampaignListVO(CampaignListAndCategory);
		responseDashboardVO.setResponseExpireSoonlistVO(result);
		responseDashboardVO.setTotalVisitor(totalVisitor);
		responseDashboardVO.setUserVO(null);
		
		AjaxResponse response = new AjaxResponse();
		response.setBody(responseDashboardVO);
		return response;
	}
	
	@PreAuthorize("isAuthenticated() or hasRole('ROLE_BLG')" )
	@GetMapping("/manage/{usrId}")
	public AjaxResponse viewBlogManagePage(@PathVariable String usrId
			, RequestExpireSoonCampaignVO requestExpireSoonCampaignVO
			, Authentication authentication) {
		if(authentication == null) {
			throw new AjaxException(null, HttpStatus.BAD_REQUEST);
		}
		
		List<CampaignPostManageVO> results = this.blogDataService.readCampaignPostByUsrId(usrId);
		
		AjaxResponse response = new AjaxResponse();
		response.setBody(results);
		
		return response;
	}
	
	@PreAuthorize("isAuthenticated() or hasRole('ROLE_BLG')" )
	@GetMapping("/details/{usrId}")
	@ResponseBody
	public AjaxResponse getBlogDetails(
	        @PathVariable String usrId) {
	    List<BlogDetailStatVO> list = blogDataService.readBlogDetailStat(usrId);
	    AjaxResponse response = new AjaxResponse();
	    response.setBody(list);
	    return response;
	}
	
	@PreAuthorize("isAuthenticated() or hasRole('ROLE_BLG')" )
	@GetMapping("/return-reason/{postId}")
	@ResponseBody
	public AjaxResponse getReturnHistory(@PathVariable String postId) {
		List<ResponseDenyHistoryVO> history = blogDataService.getReturnHistory(postId);
		AjaxResponse response = new AjaxResponse();
		response.setBody(history);
	    return response;
	}
	
	
}
