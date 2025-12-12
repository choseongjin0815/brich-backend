package com.ktdsuniversity.edu.domain.blog.vo;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktdsuniversity.edu.domain.campaign.vo.CampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.ResponseExpireSoonListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDashboardVO {
	
	private UserVO userVO;
	private ResponseExpireSoonListVO responseExpireSoonlistVO;
	private List<BlogIndexVO> BlogIndexList; 
	private List<DailyVisitorVO> DailyVisitorList;
	private List<CommonCodeVO> GoldenKeywordList;
	private List<CampaignVO> RecommendedCampaignList;
	private ResponseCampaignListVO ResponseCampaignListVO;
	private double CurrentIndex;
	private int TotalVisitor;
	
	public UserVO getUserVO() {
		return userVO;
	}
	public void setUserVO(UserVO userVO) {
		this.userVO = userVO;
	}
	public ResponseExpireSoonListVO getResponseExpireSoonlistVO() {
		return responseExpireSoonlistVO;
	}
	public void setResponseExpireSoonlistVO(ResponseExpireSoonListVO responseExpireSoonlistVO) {
		this.responseExpireSoonlistVO = responseExpireSoonlistVO;
	}
	public List<BlogIndexVO> getBlogIndexList() {
		return BlogIndexList;
	}
	public void setBlogIndexList(List<BlogIndexVO> blogIndexList) {
		BlogIndexList = blogIndexList;
	}
	public List<DailyVisitorVO> getDailyVisitorList() {
		return DailyVisitorList;
	}
	public void setDailyVisitorList(List<DailyVisitorVO> dailyVisitorList) {
		DailyVisitorList = dailyVisitorList;
	}
	public List<CommonCodeVO> getGoldenKeywordList() {
		return GoldenKeywordList;
	}
	public void setGoldenKeywordList(List<CommonCodeVO> goldenKeywordList) {
		GoldenKeywordList = goldenKeywordList;
	}
	public List<CampaignVO> getRecommendedCampaignList() {
		return RecommendedCampaignList;
	}
	public void setRecommendedCampaignList(List<CampaignVO> recommendedCampaignList) {
		RecommendedCampaignList = recommendedCampaignList;
	}
	public ResponseCampaignListVO getResponseCampaignListVO() {
		return ResponseCampaignListVO;
	}
	public void setResponseCampaignListVO(ResponseCampaignListVO responseCampaignListVO) {
		ResponseCampaignListVO = responseCampaignListVO;
	}
	public double getCurrentIndex() {
		return CurrentIndex;
	}
	public void setCurrentIndex(double currentIndex) {
		CurrentIndex = currentIndex;
	}
	public int getTotalVisitor() {
		return TotalVisitor;
	}
	public void setTotalVisitor(int totalVisitor) {
		TotalVisitor = totalVisitor;
	}
	
	
}
