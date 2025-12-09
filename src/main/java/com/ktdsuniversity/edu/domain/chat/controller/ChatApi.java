package com.ktdsuniversity.edu.domain.chat.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.chat.service.ChatService;
import com.ktdsuniversity.edu.domain.chat.vo.ChatParticipantVO;
import com.ktdsuniversity.edu.domain.chat.vo.SearchChatVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatApi {

	@Autowired
	private ChatService chatService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private static final Logger log = LoggerFactory.getLogger(ChatApi.class);
	
	/**
	 * 광고주용 채팅방 캠페인 목록 (페이징)
	 */
	@PreAuthorize("hasRole('ROLE_ADV')")
	@GetMapping("/campaigns")
	@ResponseBody
	public AjaxResponse getCampaignList(
			@RequestParam String status
		   ,@RequestParam(defaultValue = "0") int pageNo) {
		
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		SearchChatVO searchChatVO = new SearchChatVO();
		searchChatVO.setPageNo(pageNo);
		searchChatVO.setUsrId(usrId);

		SearchChatVO result = chatService.readCampaignList(searchChatVO, status);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(result);

		return ajaxResponse;
	}
	
	
	/**
	 * 채팅방 목록 조회
	 */
	@GetMapping("/rooms")
	@ResponseBody
	public AjaxResponse getChatRoomList(
			@RequestParam(required = false) String cmpnId
		  , @RequestParam(defaultValue = "0") int pageNo
		  , @RequestParam String status) {
		
		log.info("cmpnId{} : ",cmpnId);
		
		UserVO loginUser = AuthenticationUtil.getUserVO();

		String usrId = loginUser.getUsrId();
		String auth = loginUser.getAutr();

		SearchChatVO searchChatVO = new SearchChatVO();
		searchChatVO.setUsrId(usrId);
		searchChatVO.setAuth(auth);
		searchChatVO.setCmpnId(cmpnId);
		searchChatVO.setPageNo(pageNo);

		
		SearchChatVO result = null; 
		
		if(status.equals("unread")) {
			result = chatService.readUnreadChatRoomList(searchChatVO);
		}
		else {
			result = chatService.readAllChatRoomList(searchChatVO);
		}

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(result);

		return ajaxResponse;
	}

	
	/**
	 * 채팅방 나가기
	 */
	@PostMapping("/leave")
	@ResponseBody
	public AjaxResponse leaveChatRoom(@RequestBody String chtRmId) {
		
		String usrId = AuthenticationUtil.getUserVO().getUsrId();

		AjaxResponse ajaxResponse = new AjaxResponse();

		ChatParticipantVO participant = new ChatParticipantVO();
		participant.setChtRmId(chtRmId);
		participant.setUsrId(usrId);
		participant.setMttr(usrId);

		boolean success = chatService.leaveChatRoom(participant);

		Map<String, Object> result = new HashMap<>();
		result.put("success", success);
		result.put("message", success ? "채팅방을 나갔습니다." : "채팅방 나가기에 실패했습니다.");

		ajaxResponse.setBody(result);

		return ajaxResponse;
	}
	

}
