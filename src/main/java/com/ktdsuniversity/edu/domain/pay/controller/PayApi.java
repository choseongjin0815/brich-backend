package com.ktdsuniversity.edu.domain.pay.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.pay.service.PayService;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

@RequestMapping("/api/v1/pay")
@RestController
public class PayApi {
	private static final Logger log = LoggerFactory.getLogger(PayController.class);
	
	@Autowired
    private PayService payService;
	
	@Autowired 
	private CampaignService campaignService;
	
	@PostMapping("/subscribeInfo")
    public AjaxResponse subscribePayPage() {
		
    	UserVO loginUser = AuthenticationUtil.getUserVO();
    	List<CommonCodeVO> commonCodeVoList = this.payService.payInfoServiceList();
    	
    	log.info("상품정보 : " + commonCodeVoList.toString());
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(commonCodeVoList);
    	
    	return ajaxResponse;
    }
}
