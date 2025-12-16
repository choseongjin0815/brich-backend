package com.ktdsuniversity.edu.domain.faq.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.faq.service.FaqService;
import com.ktdsuniversity.edu.domain.faq.vo.response.ResponseFaqVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

@RestController
@RequestMapping("/api/v1/faq")
public class FaqApi {
    @Autowired
    private FaqService faqService;
    
    @GetMapping
    public AjaxResponse getFaqList(@RequestParam(required=false) String category
    						) {
    	AjaxResponse ajaxResponse = new AjaxResponse();
    	
    	ResponseFaqVO faqResponse = this.faqService.selectFaqList(category);
    	    	
    	ajaxResponse.setBody(faqResponse);
    	
    	return ajaxResponse;
    }

}
