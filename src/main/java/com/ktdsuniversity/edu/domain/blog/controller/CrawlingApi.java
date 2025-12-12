package com.ktdsuniversity.edu.domain.blog.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.blog.dao.PostDataDao;
import com.ktdsuniversity.edu.domain.blog.service.BlogDataService;
import com.ktdsuniversity.edu.domain.blog.vo.PostDataInsertVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestBlogInfoVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestBlogTitleVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestModifyBlogAddrsVO;
import com.ktdsuniversity.edu.domain.blog.vo.RequestPostDataResultVO;
import com.ktdsuniversity.edu.domain.user.service.UserService;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/crawling")
public class CrawlingApi {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogDataService blogDataService;
    
    private static final Logger log = LoggerFactory.getLogger(CrawlingApi.class);
    
    /**
     * 1) 첫 포스트 데이터 저장
     */
    @PostMapping("/results")
    public AjaxResponse responseInitPostData(@RequestBody RequestPostDataResultVO request) {

        for (PostDataInsertVO post : request.getPostList()) {
            post.setBlgAddrs(request.getBlgAddrs());
            blogDataService.insertPostData(post);
        }

        AjaxResponse response = new AjaxResponse();
        		
                response.setBody(Map.of(
                    "success", true,
                    "message", "포스트 데이터 저장 완료"
               ));
                return response;
    }


    /**
     * 2) 인증 코드 생성
     */
    @PostMapping("/verify-code")
    public AjaxResponse generateVerificationCode(HttpSession session) {

        String code = blogDataService.generateVerificationCode();
        session.setAttribute("VERIFY_CODE", code);

        AjaxResponse response = new AjaxResponse();
		
        response.setBody(Map.of(
                    "success", true,
                    "code", code
                ));
        return response;
    }


    /**
     * 3) 블로그 인증
     */
    @PostMapping("/verify")
    public AjaxResponse responseVerifyBlogger(
            @RequestBody Map<String, String> request, HttpSession session) {
        String blogUrl = request.get("blogUrl");
        String usrId   = request.get("usrId");

        RequestModifyBlogAddrsVO blogInfo = new RequestModifyBlogAddrsVO();
        blogInfo.setBlgAddrs(blogUrl);
        blogInfo.setId(usrId);
        log.info("usrId = {}", usrId);
        log.info("blogUrl = {}", blogUrl);
        String code = (String) session.getAttribute("VERIFY_CODE");

        boolean verified = blogDataService.runPythonVerification(blogInfo, code);

        if (!verified) {
            AjaxResponse response = new AjaxResponse();
    		
            response.setBody(Map.of(
                        "success", false,
                        "message", "블로그 인증 실패"
                    ));
            return response;
        }

        // 세션 유저 업데이트
        UserVO loginUser = (UserVO) session.getAttribute("__LOGIN_USER__");
        if (loginUser != null) {
            loginUser.setBlgAddrs(blogUrl);
        }

        // 비동기 처리
        CompletableFuture.runAsync(() ->
                blogDataService.runPythonInitialPostData(blogUrl)
        );

        blogDataService.runPythonBlogStats(blogUrl);
        blogDataService.runPythonBlogTitle(blogUrl);

        AjaxResponse response = new AjaxResponse();
		
        response.setBody(Map.of(
                    "success", true,
                    "message", "블로그 인증 및 초기 데이터 수집 완료"
                ));
        return response;
    }


    /**
     * 4) 블로그 통계 저장
     */
    @PostMapping("/stats")
    public AjaxResponse responseBlogInfo(@RequestBody RequestBlogInfoVO request){
        userService.updateBlogInfo(request);

        AjaxResponse response = new AjaxResponse();
		
        response.setBody(Map.of(
                    "success", true
                ));
        return response;
    }


    /**
     * 5) 블로그 제목 저장
     */
    @PostMapping("/title")
    public AjaxResponse responseBlogTitle(@RequestBody RequestBlogTitleVO request){

        boolean updated = userService.updateBlogTitle(request);

        AjaxResponse response = new AjaxResponse();
		
        response.setBody(Map.of(
                    "success", updated
                ));
        return response;
    }
}

