package com.ktdsuniversity.edu.domain.inqr.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.inqr.service.InqrService;
import com.ktdsuniversity.edu.domain.inqr.vo.InqrSearchVO;
import com.ktdsuniversity.edu.domain.inqr.vo.InqrVO;
import com.ktdsuniversity.edu.domain.inqr.vo.request.RequestInqrCreateVO;
import com.ktdsuniversity.edu.domain.inqr.vo.response.ResponseInqrVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.exceptions.AjaxException;

@RestController
@RequestMapping("/api/v1/inqr")
public class InqrApi {
	private static final Logger log = LoggerFactory.getLogger(InqrApi.class);

    @Autowired
    private InqrService inqrService;
	
    //문의글 리스트
	@GetMapping
	public AjaxResponse getInqrList(InqrSearchVO inqrSearchVO) {
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		
        List<InqrVO> inqrList = this.inqrService.selectMyInqrListWithPaging(inqrSearchVO);
    	
        ajaxResponse.setBody(Map.of("inqrList", inqrList, "inqrSearch", inqrSearchVO));
        
    	return ajaxResponse;
	}
	
	//문의글 작성
	@PostMapping
	public AjaxResponse createInqr(RequestInqrCreateVO requestInqrCreateVO) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		
    	boolean createResult = this.inqrService.createNewInqr(requestInqrCreateVO);
    	
    	if(createResult) {
    		ajaxResponse.setBody("문의 접수가 성공적으로 접수되었습니다!");
    	}
    	else {
    		ajaxResponse.setBody("문의 접수에 실패했습니다.");
    	} 
		
		return ajaxResponse;
	}
	//문의글 상세 조회
	@GetMapping("/{inqrId}")
	public AjaxResponse getInqrDetail(@PathVariable String inqrId) {
		log.info("view inqrId: {}",inqrId);
		AjaxResponse ajaxResponse = new AjaxResponse();
		
		ResponseInqrVO inqr = this.inqrService.readInqrDetailByInqrId(inqrId);
		
		if(inqr == null) {
			throw new AjaxException(null, HttpStatus.NOT_FOUND, Map.of("message", "문의글을 찾을 수 없습니다."));
		}
		
		ajaxResponse.setBody(inqr);
		
		return ajaxResponse;
		
	}
	
	//문의글 카테고리 목록 조회
	@GetMapping("/category")
	public AjaxResponse getInqrCategories() {
		AjaxResponse ajaxResponse = new AjaxResponse();
    	List<CommonCodeVO> inqrCatergory = this.inqrService.readInqrCategory();
    	
    	ajaxResponse.setBody(inqrCatergory);

		return ajaxResponse;
	}
	
	
	
}
