package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.ktdsuniversity.edu.domain.campaign.vo.request.RequestPostSubmitVO;
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
import com.ktdsuniversity.edu.global.exceptions.BrichException;
import com.ktdsuniversity.edu.global.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

@RequestMapping("/api/v1/campaign")
@RestController
public class CampaignApi {

    private final JwtProvider jwtProvider;
	private static final Logger log = LoggerFactory.getLogger(CampaignApi.class);
	
	@Autowired
	private CampaignService campaignService;
	@Autowired
    private BlogDataService blogDataService;

    CampaignApi(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
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
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/submitted")
    public AjaxResponse submittedmycampaign() {
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readSubmittedMyCampaignByBlgId(blgId);
    	
    	log.info( " 신청한 캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory.getResponseCampaignList());
    	return ajaxResponse;
    	}
    
    /**
     * blog My캠페인 - 진행중 캠페인
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/ongoing")
    public AjaxResponse campaignongoing() {
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readOnGoingMyCampaignByBlgId(blgId);
    	
    	log.info( "진행중 캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory.getResponseCampaignList());
    	return ajaxResponse;
    }
    
    /**
     * blog My캠페인 - 마감된 캠페인
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/closed")
    public AjaxResponse closedcampaign() {
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readClosedMyCampaignByBlgId(blgId);
    	
    	log.info( "마감된 캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory.getResponseCampaignList());
    	return ajaxResponse;
    }
    
    /**
     * blog My캠페인 - 관심 캠페인
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/fav")
    public AjaxResponse favcampaign() {
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	ResponseCampaignListVO CampaignListAndCategory = campaignService.readFavMyCampaignByBlgId(blgId);
    	
    	log.info( "관심 캠페인 리스트 조회결과 : " + CampaignListAndCategory.getResponseCampaignList().toString());
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(CampaignListAndCategory.getResponseCampaignList());
    	return ajaxResponse;
    }
    

    /**
     * 캠페인 신청, 취소 하기
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/apply")
    public AjaxResponse applyCampaign(@RequestBody RequestPostSubmitVO requestPostSubmitVO) {
    	
    	String blgId = AuthenticationUtil.getUserVO().getUsrId();
    	int count = this.campaignService.applyCampaign(requestPostSubmitVO.getCmpnId(), blgId);
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(count);
    	
    	return ajaxResponse;
    }
    
    /**
     * 포스팅 작성
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping("/blgr/pstsubmit")
    public AjaxResponse postSubmit(@RequestBody RequestPostSubmitVO requestPostSubmitVO) {
    	
    	requestPostSubmitVO.setBlgId( AuthenticationUtil.getUserVO().getUsrId());
    	log.info( "포스팅 작성 input 정보 : " + requestPostSubmitVO.toString());
    	int count = this.campaignService.postSubmit(requestPostSubmitVO);
    
    	
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	ajaxResponse.setBody(count);
    	
    	return ajaxResponse;
    }
    
    /**
     * 포스팅 재 제출
     * @return
     */
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BLG')")
    @PostMapping(value = "/blgr/repstsubmit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResponse rePostSubmit(@ModelAttribute RequestPostSubmitVO requestPostSubmitVO) {

    	requestPostSubmitVO.setBlgId(AuthenticationUtil.getUserVO().getUsrId());

        log.info("포스팅 input 정보 : {}", requestPostSubmitVO);

        int count = this.campaignService.rePostSubmit(requestPostSubmitVO);

        AjaxResponse ajaxResponse = new AjaxResponse();
        ajaxResponse.setBody(count); // 보통 count 반환
        return ajaxResponse;
    }

    // ////////////////////////// Hapa up!
    
    
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
	public AjaxResponse doReadDenyHistoryAction(@PathVariable String cmpnPstAdptId) {
		List<ResponseDenyHistoryVO> history = this.campaignService.readDenyHistoryByCmpnPstAdptId(cmpnPstAdptId);

		AjaxResponse response = new AjaxResponse();
		response.setBody(history);
		
		return response;
	}
	
	@PutMapping("/post-approve/{cmpnPstAdptId}")
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
	
	@PostMapping("/post-deny")
	public boolean doCreateDenyAction(RequestDenyVO requestDenyVO,
									  Authentication authentication) {
//		requestDenyVO.setAdvId(loginUser.getUsrId());
		requestDenyVO.setAdvId("USR-20240413-000007");
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
	public AjaxResponse createCampaign(Authentication authentication) {
//		if (!loginUser.getAutr().equals("1004")) {
//			throw new BrichException("잘못된 접근입니다.", "error/403");
//		}
		
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
	public boolean doCreateNewCampaignAction(@Valid RequestCreateCmpnVO requestCreateCmpnVO,
											BindingResult bindingResult,
											Authentication authentication) {
//		if (!loginUser.getAutr().equals("1004")) {
//			throw new BrichException("잘못된 접근입니다.", "error/403");
//		}
		
		if (bindingResult.hasErrors()) {
			throw new BrichException("인자가 부족합니다.", "error/403");
		}
		
//		requestCreateCmpnVO.setUsrId(loginUser.getUsrId());
		requestCreateCmpnVO.setUsrId("USR-20240413-000007");
		
		boolean insert = this.campaignService.createNewCampaign(requestCreateCmpnVO);
		return insert;
	}
	
	@GetMapping("/modify")
	public AjaxResponse modifyCampaign(String cmpnId,
									   Authentication authentication) {
		ResponseModifyCampaignVO responseModifyCampaignVO = this.campaignService.readModifyInfoByCmpnId(cmpnId);

		log.info("---modfiy:" + responseModifyCampaignVO);
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(responseModifyCampaignVO);
		
		return ajaxResponse;
	}
	
	@PostMapping("/modify")
	public boolean doModifyCampaignAction(@Valid RequestCreateCmpnVO requestCreateCmpnVO,
										 BindingResult bindingResult,
										 Authentication authentication) {
//		if (!loginUser.getAutr().equals("1004")) {
//			throw new BrichException("잘못된 접근입니다.", "error/403");
//		}
		
		if (bindingResult.hasErrors()) {
			throw new BrichException("인자가 부족합니다.", "error/500");
		}
		
//		requestCreateCmpnVO.setUsrId(loginUser.getUsrId());
		requestCreateCmpnVO.setUsrId("USR-20240413-000007");
		log.info("---requestCreateCmpnVO" + requestCreateCmpnVO.toString());
		boolean modify = this.campaignService.modifyNewCampaign(requestCreateCmpnVO);
		return modify;
	}
	
	@PostMapping("/temporary")
	public boolean doCreateTemporaryCampaignAction(RequestCreateCmpnVO requestCreateCmpnVO
												 ,Authentication authentication) {
//		requestCreateCmpnVO.setUsrId(loginUser.getUsrId());
		requestCreateCmpnVO.setUsrId("USR-20240413-000007");
		
		log.info("--temporary--" + requestCreateCmpnVO.toString());
		boolean create = this.campaignService.createTemporaryCampaign(requestCreateCmpnVO);
		
		return create;
	}
}
