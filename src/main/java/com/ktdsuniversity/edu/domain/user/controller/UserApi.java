package com.ktdsuniversity.edu.domain.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignwriteVO;
import com.ktdsuniversity.edu.domain.user.service.UserService;
import com.ktdsuniversity.edu.domain.user.vo.request.RequestUserRegistVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserApi {

	@Autowired
	private UserService userService;
	
	private static final Logger log = LoggerFactory.getLogger(UserApi.class);

	
	//회원가입
	@PostMapping
	public AjaxResponse createUser(@Valid RequestUserRegistVO requestUserRegistVO
			   , BindingResult bindingResult
			   , @RequestParam String roleType) {
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		
    	boolean registResult = this.userService.createNewUser(requestUserRegistVO);
		
		if(registResult && roleType.equals("blogger")) {
			ajaxResponse.setBody("회원가입이 완료되었습니다!");
		}
		else if(registResult && roleType.equals("advertiser")) {
			ajaxResponse.setBody("회원가입 신청이 완료되었습니다!");
		}
		
		return ajaxResponse;
	}
	/**
     * 이미 등록된 아이디인지 체크
     * @param logId
     * @return 0: 일치하는 아이디 없음(성공), 1: 일치하는 아이디 있음(실패), 2: 요구하는 형식에 부합하지 않음
     */
    @GetMapping("/duplicate")
    @ResponseBody
    public AjaxResponse checkLogId (@RequestParam String logId) {
    	log.info("logId : {}", logId);
    	int count = this.userService.readUserIdByLogId(logId);
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(count);
    	
    	if (!logId.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{8,16}$")) {
    		ajaxResponse.setBody(2);
    	} 
    	
    	return ajaxResponse;
    }
}
