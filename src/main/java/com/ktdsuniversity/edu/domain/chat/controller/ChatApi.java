package com.ktdsuniversity.edu.domain.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.domain.campaign.vo.CampaignVO;
import com.ktdsuniversity.edu.domain.chat.service.ChatService;
import com.ktdsuniversity.edu.domain.chat.vo.ChatMessageVO;
import com.ktdsuniversity.edu.domain.chat.vo.ChatParticipantVO;
import com.ktdsuniversity.edu.domain.chat.vo.SearchChatVO;
import com.ktdsuniversity.edu.domain.chat.vo.request.RequestChatMessageVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;
import com.ktdsuniversity.edu.global.exceptions.AjaxException;
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
	@GetMapping
	public AjaxResponse getChatRoomList(
			@RequestParam(required = false) String cmpnId
		  , @RequestParam(defaultValue = "0") int pageNo
		  , @RequestParam String status) {
		
		
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
	 * 채팅방 기본정보
	 */
	@GetMapping("/{chtRmId}")
	public AjaxResponse  viewChatRoomPage(@PathVariable String chtRmId) {
		AjaxResponse ajaxResponse = new AjaxResponse();

		CampaignVO campaign = this.chatService.readCampaignByChtRmId(chtRmId);
		
		if(campaign == null) {
			throw new AjaxException(null, HttpStatus.NOT_FOUND, Map.of("message", "존재하지 않는 채팅방입니다."));
		}

		ajaxResponse.setBody(campaign);
		
		return ajaxResponse;

	}
	
	
	/**
	 * 채팅방 나가기
	 */
	@PostMapping("/leave")
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
	
	
	/**
	 * 채팅방 입장 시 메시지 목록 조회 (페이징)
	 */
	@GetMapping("/messages")
	public AjaxResponse getChatMessages(
			@RequestParam String chtRmId
		  , @RequestParam(defaultValue = "0") int page
		  , @RequestParam(defaultValue = "20") int size) {

		String usrId = AuthenticationUtil.getUserVO().getUsrId();

		Map<String, Object> result = chatService.readChatMessageListPaged(chtRmId, usrId, page, size);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(result);

		return ajaxResponse;
	}

	/**
	 * 메시지 전송 파일이 있는 경우
	 */
	@PostMapping("/message")
	public AjaxResponse sendMessage(RequestChatMessageVO requestChatMessageVO) {
		log.info("requestmessage{}", requestChatMessageVO.getChtRmId());
		
		AjaxResponse ajaxResponse = new AjaxResponse();

		ChatMessageVO savedMessage = chatService.sendMessage(requestChatMessageVO);

		// WebSocket으로 다른 참가자에게 메시지 전송
		messagingTemplate.convertAndSend("/topic/chat/" + requestChatMessageVO.getChtRmId(), savedMessage);

		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		result.put("data", savedMessage);

		ajaxResponse.setBody(result);

		return ajaxResponse;
	}

	/*
	 * 구독할 채팅방 ID 리스트 
	 */
	@GetMapping("/my-rooms")
	public AjaxResponse getMyChatRoomIdList() {
		AjaxResponse ajaxResponse = new AjaxResponse();
		String usrId = AuthenticationUtil.getUserVO().getUsrId();
		
		List<String> roomIds = chatService.readMyRoomIds(usrId);
		
		ajaxResponse.setBody(roomIds);
		
		return ajaxResponse;
	}
	
	
	/**
	 * WebSocket 메시지 핸들러 - 채팅 메시지 전송
	 */
	@MessageMapping("/chat/send")
	public void handleChatMessage(@Payload RequestChatMessageVO requestChatMessageVO) {
		// 메시지 저장 (파일 없이)
		ChatMessageVO savedMessage = chatService.sendMessage(requestChatMessageVO);

		// TODO JS Stomp Client 사용해 테스트, Postman 등으로는 테스트 어려움
		// 채팅방의 모든 참가자에게 메시지 전송
		messagingTemplate.convertAndSend("/topic/chat/" + requestChatMessageVO.getChtRmId(), savedMessage);
	}

	/**
	 * WebSocket 메시지 핸들러 - 읽음 처리
	 */
	@MessageMapping("/chat/read")
	public void handleReadMessage(@Payload Map<String, String> payload) {
		String chtRmId = payload.get("chtRmId");
		String usrId = payload.get("usrId");

		chatService.updateMessagesAsRead(chtRmId, usrId);

		// 읽음 처리 알림을 채팅방 참가자에게 전송
		messagingTemplate.convertAndSend("/topic/chat/" + chtRmId + "/read", payload);
	}
	
	

}
