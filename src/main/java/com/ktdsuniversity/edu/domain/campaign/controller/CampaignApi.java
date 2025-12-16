package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.blog.service.BlogDataService;
import com.ktdsuniversity.edu.domain.campaign.service.CampaignService;
import com.ktdsuniversity.edu.domain.campaign.vo.ResponseModifyCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestApplicantVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestCreateCmpnVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestDenyVO;
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestSearchCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseAdoptListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseApplicantListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignListVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseCampaignwriteVO;
import com.ktdsuniversity.edu.domain.campaign.vo.response.ResponseDenyHistoryVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.exceptions.AjaxException;
import com.ktdsuniversity.edu.global.exceptions.BrichException;
import com.ktdsuniversity.edu.global.security.jwt.JwtProvider;
import com.ktdsuniversity.edu.global.security.jwt.controller.JwtAuthenticationController;
import jakarta.validation.Valid;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

@RequestMapping("/api/v1/campaign")
@RestController
public class CampaignApi {

    private final JwtAuthenticationController jwtAuthenticationController;

    private final JwtProvider jwtProvider;
	private static final Logger log = LoggerFactory.getLogger(CampaignApi.class);
	
	@Autowired
	private CampaignService campaignService;
	@Autowired
    private BlogDataService blogDataService;

    CampaignApi(JwtProvider jwtProvider, JwtAuthenticationController jwtAuthenticationController) {
        this.jwtProvider = jwtProvider;
        this.jwtAuthenticationController = jwtAuthenticationController;
    }

	// Hapa ===============================================================
	/**
	 * 캠페인 메인 리스트 조회
	 * @param requestSearchCampaignVO
	 * @return
	 */
//    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/main")
    public AjaxResponse campaignMainPage(@RequestBody RequestSearchCampaignVO requestSearchCampaignVO){
    	
    	// 로그인 했으면 UserVO, 아니면 null
        UserVO user = AuthenticationUtil.getUserVO();

        String loginId = null;
        if (user != null) {
            loginId = user.getUsrId();
        }
    	
//    	String loginId = AuthenticationUtil.getUserVO().getUsrId();
//    	Object loginId = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    	log.info( "뭐냐:ㅣ" + loginId);
    	requestSearchCampaignVO.setLoginId(loginId);
        
        
    	log.info( "입력 파라미터 값 : " + requestSearchCampaignVO.toString());
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readCampaignListAndCategory(requestSearchCampaignVO);
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory);
    	
    	
    	log.info( "캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	return ajaxResponse;
    }  
    
    /**
     * 캠페인 상세조회
     * @param requestSearchCampaignVO
     * @return
     */
    @PostMapping("/detail")
    public AjaxResponse campaignDetailPage(@RequestBody RequestSearchCampaignVO requestSearchCampaignVO) {
    	String campaignId = requestSearchCampaignVO.getCmpnId();
    	ResponseCampaignVO detail = new ResponseCampaignVO();
    	
    	UserVO loginUser = new UserVO();
    	if(AuthenticationUtil.getUserVO() != null) {
    		loginUser.setAutr(AuthenticationUtil.getUserVO().getAutr());
    		loginUser.setUsrId(AuthenticationUtil.getUserVO().getUsrId());
    	}
    	
    	if(loginUser.getAutr() != null) {
    		if(loginUser.getAutr().contains("ROLE-20251203-000002") || loginUser.getAutr().contains("ROLE-20251203-000003")) {
        		detail = campaignService.readCampaignDetail(campaignId, loginUser.getUsrId());    	
        		String returnReason =  campaignService.postReturnReason(campaignId, loginUser.getUsrId());
        		detail.setReturnReason(returnReason);
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
    
    
    
 // 석진 ===============================================================
 	@GetMapping("list")
 	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
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
 	
    /**  
     * 좋아요(즐겨찾기) do undo 
     * @param loginUser
     * @param campaignId
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/dolove")
    public AjaxResponse favCampaignDo( @RequestBody RequestSearchCampaignVO requestSearchCampaignVO) {
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	int count = campaignService.favCampaignDo(blgId, requestSearchCampaignVO.getCmpnId());
    	System.out.println("doLove 작동!!");
    	
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(count);
    	return ajaxResponse;
    }
    /**
     * blog My캠페인 - 신청한 캠페인
     * @param model
     * @param loginUser
     * @return
     */
    @PostMapping("/blgr/submitted")
    public String submittedmycampaign(Model model,@SessionAttribute(value = "__LOGIN_USER__") 
    						UserVO loginUser) {
    	String blgId = loginUser.getUsrId();
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readSubmittedMyCampaignByBlgId(blgId);
    	model.addAttribute("campaignList", CampaignListAndCategory.getResponseCampaignList());
    	
    	log.info( "캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	return "campaign/submittedmycampaign";
    }
    
    // ////////////////////////// Hapa up!
    
	@GetMapping("/applicant")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
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
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
    public boolean doUpdateAdptYnAction(@RequestBody RequestApplicantVO requestApplicantVO,
    									Authentication authentication) {
		UserVO user = (UserVO) authentication.getPrincipal();
    	requestApplicantVO.setUsrId(user.getUsrId());
    	boolean update = this.campaignService.updateAdptYnByCmpnPstAdptId(requestApplicantVO);
    	
    	if (update) {
    		return true;
    	}
    	
    	else {
    		return false;
    	}
    }
	
	@GetMapping("/adopt")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
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
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public AjaxResponse doReadDenyHistoryAction(@PathVariable String cmpnPstAdptId) {
		List<ResponseDenyHistoryVO> history = this.campaignService.readDenyHistoryByCmpnPstAdptId(cmpnPstAdptId);

		AjaxResponse response = new AjaxResponse();
		response.setBody(history);
		
		return response;
	}
	
	@PutMapping("/post-approve/{cmpnPstAdptId}")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
    public boolean doUpdatePstSttsApproveAction(@PathVariable String cmpnPstAdptId,
    											Authentication authentication) {
    	RequestApplicantVO requestApplicantVO = new RequestApplicantVO();
    	requestApplicantVO.setCmpnPstAdptId(cmpnPstAdptId);
    	
    	UserVO user = (UserVO) (authentication.getPrincipal());
    	requestApplicantVO.setUsrId(user.getUsrId());
    	boolean update = this.campaignService.updatePstSttsApproveByCmpnPstAdoptId(requestApplicantVO);
    	
    	if (update) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
	
	@PostMapping("/post-deny")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public boolean doCreateDenyAction(RequestDenyVO requestDenyVO,
									  Authentication authentication) {
		UserVO user = (UserVO) (authentication.getPrincipal());
		requestDenyVO.setAdvId(user.getUsrId());
		log.info("------ requestDenyVO:" + requestDenyVO.toString());
		boolean insert = this.campaignService.createDenyByCmpnPstAdoptId(requestDenyVO);
		
		if (insert) {
    		return true;
    	}
    	else {
    		return false;
    	}
	}
	
	@GetMapping("/write")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public AjaxResponse createCampaign(Authentication authentication) {
		ResponseCampaignwriteVO common = this.campaignService.createCampaign();
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(common);
		return ajaxResponse;
	}
	
	@GetMapping("/{cdId}")
	public AjaxResponse doReadDistrictAction(@PathVariable String cdId) {
		AjaxResponse response = new AjaxResponse();
		
		List<CommonCodeVO> districtList = this.campaignService.readDistrictByCdId(cdId);
		response.setBody(districtList);
		return response;
	}
	
	@PostMapping("/write")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public boolean doCreateNewCampaignAction(@Valid RequestCreateCmpnVO requestCreateCmpnVO,
											BindingResult bindingResult,
											Authentication authentication) {
		
		if (bindingResult.hasErrors()) {
			throw new AjaxException(null, HttpStatus.BAD_REQUEST, bindingResult.getFieldErrors());
		}
		
		UserVO user = (UserVO) authentication.getPrincipal();
		requestCreateCmpnVO.setUsrId(user.getUsrId());
		
		boolean insert = this.campaignService.createNewCampaign(requestCreateCmpnVO);
		return insert;
	}
	
	@GetMapping("/modify")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public AjaxResponse modifyCampaign(String cmpnId,
									   Authentication authentication) {
		ResponseModifyCampaignVO responseModifyCampaignVO = this.campaignService.readModifyInfoByCmpnId(cmpnId);

		log.info("---modfiy:" + responseModifyCampaignVO);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(responseModifyCampaignVO);
		
		return ajaxResponse;
	}
	
	@PostMapping("/modify")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public boolean doModifyCampaignAction(@Valid RequestCreateCmpnVO requestCreateCmpnVO,
										 BindingResult bindingResult,
										 Authentication authentication) {
	
		if (bindingResult.hasErrors()) {
			throw new AjaxException(null, HttpStatus.BAD_REQUEST, bindingResult.getFieldErrors());
		}
		
		UserVO user = (UserVO) authentication.getPrincipal();
		requestCreateCmpnVO.setUsrId(user.getUsrId());
		log.info("---requestCreateCmpnVO" + requestCreateCmpnVO.toString());
		boolean modify = this.campaignService.modifyNewCampaign(requestCreateCmpnVO);
		return modify;
	}
	
	@PostMapping("/temporary")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public boolean doCreateTemporaryCampaignAction(RequestCreateCmpnVO requestCreateCmpnVO,
												   Authentication authentication) {
		UserVO user = (UserVO) authentication.getPrincipal();
		requestCreateCmpnVO.setUsrId(user.getUsrId());
		
		log.info("--temporary--" + requestCreateCmpnVO.toString());
		boolean create = this.campaignService.createTemporaryCampaign(requestCreateCmpnVO);
		
		return create;
	}
	
	@GetMapping("deny-history/{cmpnId}")
	@PreAuthorize("isAuthenticated() and hasRole('ROLE_ADV')")
	public AjaxResponse readDenyHistory(@PathVariable String cmpnId,
								  Authentication authentication) {
		ResponseCampaignListVO denyList = this.campaignService.readDenyHistoryByCmpnId(cmpnId);
		log.info("---denyList:" + denyList.toString());
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(denyList);
		
		return ajaxResponse;
	}
}
