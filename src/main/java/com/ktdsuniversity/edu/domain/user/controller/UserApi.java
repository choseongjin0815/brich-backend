package com.ktdsuniversity.edu.domain.user.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignwriteVO;
import com.ktdsuniversity.edu.domain.user.service.UserService;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserAccountPasswordVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserFindIdVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserInfoModifyVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserRegistVO;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserResetPasswordVO;
import com.ktdsuniversity.edu.domain.user.vo.response.ResponseUserInfoVO;
import com.ktdsuniversity.edu.domain.user.vo.response.ResponseUserSubscriptionInfoVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

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
	
	@PutMapping("/role")
	public AjaxResponse updateRole(@RequestParam String role) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		String usrId = AuthenticationUtil.getUserVO().getUsrId(); 
		boolean updateResult = this.userService.updateRoleByUsrId(usrId, role);
		ajaxResponse.setBody(updateResult);
		
		return ajaxResponse;
	}
	
	/**
	 * 계정 정보 가져오기 
	 */
	@GetMapping("/account")
	public AjaxResponse getAccountInfo() {
		AjaxResponse ajaxResponse = new AjaxResponse();
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		
		ResponseUserInfoVO userInfo = this.userService.readUserByUserId(usrId);
		ResponseCampaignwriteVO common = userInfo.getResponseCampaignwriteVO();
		
		ajaxResponse.setBody(Map.of("userInfo", userInfo, "common", common));
		
		return ajaxResponse;
	}
	
	//TODO 필요할지 체크 필요
	@GetMapping("/account/{cdId}")
	public AjaxResponse getDistrictAction(@PathVariable String cdId) {
		AjaxResponse response = new AjaxResponse();
		
		List<CommonCodeVO> districtList = this.campaignService.readDistrictByCdId(cdId);
		response.setBody(districtList);
		return response;
	}
	
	/*
	 * 계정 정보 수정 
	 */
	@PutMapping("/account")
	public AjaxResponse updateUserInfo(RequestUserInfoModifyVO requestUserInfoModifyVO) {
		log.info("account update {}", requestUserInfoModifyVO);
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		requestUserInfoModifyVO.setUsrId(usrId);
		boolean updateResult = this.userService.updateUserInfoByUsrId(requestUserInfoModifyVO);
		AjaxResponse ajaxResponse = new AjaxResponse();
		
		if(updateResult == false) {
			ajaxResponse.setBody("수정에 실패했습니다.");
			return ajaxResponse;
		}
		ajaxResponse.setBody("수정에 성공했습니다.");
		
		return ajaxResponse;
	}
	
	/**
	 * 비밀번호 재설정 요청
	 */
	@PutMapping("/reset-password")
	public AjaxResponse resetPassword(RequestUserAccountPasswordVO requestUserAccountPasswordVO) {
		
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		requestUserAccountPasswordVO.setUsrId(usrId);
		log.info("{}", requestUserAccountPasswordVO);
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		
		if(requestUserAccountPasswordVO.getCurrentPswrd().equals("")|| 
		   requestUserAccountPasswordVO.getNewPswrd().equals("")) {
		   ajaxResponse.setBody(Map.of("result", "모든 값을 입력해야합니다."));
    	   return ajaxResponse;
		} 
		
		if (!requestUserAccountPasswordVO.getNewPswrd().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,16}$")) {
    		ajaxResponse.setBody(Map.of("result", "비밀번호는 8~16자까지 가능하며 영문자와 숫자를 혼합해야합니다."));
    		return ajaxResponse;
    	} 
		
		if(!requestUserAccountPasswordVO.getNewPswrd().equals(requestUserAccountPasswordVO.getNewPswrdConfirm())) {
			ajaxResponse.setBody(Map.of("result", "비밀번호가 일치하지 않습니다."));
			return ajaxResponse;
		}
		
		boolean updatePassword = this.userService.updatePswrdByUsrId(requestUserAccountPasswordVO);
		
		//입력한 기존 비밀번호가 DB에 저장된 비밀번호와 같지 않은 경우
		if(updatePassword == false) {
			ajaxResponse.setBody(Map.of("result", "현재 비밀번호가 일치하지 않습니다."));
			return ajaxResponse;
		}
		
		ajaxResponse.setBody(Map.of("result", "비밀번호가 재설정 되었습니다!"));
		
		
		return ajaxResponse;
	}
	
	/**
	 * 구독 정보
	 */
	@GetMapping("/subscription-info") 
	public AjaxResponse getSubscriptionInfo() {
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		AjaxResponse ajaxResponse = new AjaxResponse();
	

		ResponseUserSubscriptionInfoVO subscriptionInfo = this.userService.readSubscriptionInfoByUserId(usrId);
		log.info("subInfo:  {}", subscriptionInfo);
		ajaxResponse.setBody(subscriptionInfo);
	
		
		return ajaxResponse;
	}
	
	
}
