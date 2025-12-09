package com.ktdsuniversity.edu.domain.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignwriteVO;
import com.ktdsuniversity.edu.domain.user.service.UserService;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserFindIdVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserRegistVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserResetPasswordVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserApi {

	@Autowired
	private UserService userService;
	@Autowired
	private CampaignService campaignService;

	private static final Logger log = LoggerFactory.getLogger(UserApi.class);

	// 회원가입
	@PostMapping
	public AjaxResponse createUser(@Valid RequestUserRegistVO requestUserRegistVO, BindingResult bindingResult,
			@RequestParam String roleType) {

		AjaxResponse ajaxResponse = new AjaxResponse();
		boolean registResult = this.userService.createNewUser(requestUserRegistVO);

		if (registResult && roleType.equals("blogger")) {
			ajaxResponse.setBody("회원가입이 완료되었습니다!");
		} else if (registResult && roleType.equals("advertiser")) {
			ajaxResponse.setBody("회원가입 신청이 완료되었습니다!");
		}

		return ajaxResponse;
	}

	/**
	 * 이미 등록된 아이디인지 체크
	 * 
	 * @param logId
	 * @return 0: 일치하는 아이디 없음(성공), 1: 일치하는 아이디 있음(실패), 2: 요구하는 형식에 부합하지 않음
	 */
	@GetMapping("/duplicate")
	public AjaxResponse checkLogId(@RequestParam String logId) {
		int count = this.userService.readUserIdByLogId(logId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(count);

		if (!logId.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,16}$")) {
			ajaxResponse.setBody(2);
		}

		return ajaxResponse;
	}

	/*
	 * 회원가입, 회원 정보 수정에서 사용할 카테고리와 지역 리스트
	 */
	@GetMapping("/categories-and-cities")
	public AjaxResponse getCategoriesAndCities() {
		ResponseCampaignwriteVO meta = this.campaignService.createCampaign();
		AjaxResponse ajaxResponse = new AjaxResponse();

		ajaxResponse.setBody(meta);

		return ajaxResponse;
	}

	/*
	 * 아이디 찾기
	 */
	@GetMapping("/id")
	public AjaxResponse getLogId(@RequestParam String nm, @RequestParam String eml) {

		AjaxResponse ajaxResponse = new AjaxResponse();
		RequestUserFindIdVO requestUserFindIdVO = new RequestUserFindIdVO();
		
		requestUserFindIdVO.setEml(eml);
		requestUserFindIdVO.setNm(nm);
		
		String logId = this.userService.readLogIdByNameAndEmail(requestUserFindIdVO);
		log.info("logId{}",logId);

		int start = 3;
		int end = 6;

		String masked = logId.substring(0, start) // 앞부분 그대로
				+ "****" // 중간은 마스킹
				+ logId.substring(end); // 나머지 그대로
		
		ajaxResponse.setBody(masked);

		return ajaxResponse;
	}
	
	@PutMapping("/password")
	public AjaxResponse updatePassword(@RequestBody RequestUserResetPasswordVO resetPasswordInfo) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		log.info("password{}, result{}", resetPasswordInfo.getPswrd());

    	boolean updateResult = this.userService.updatePswrdByLogIdAndPswrd(resetPasswordInfo);   	

		log.info("password{}, result{}", resetPasswordInfo.getPswrd(), updateResult);

    	
		return ajaxResponse;
	}
}
