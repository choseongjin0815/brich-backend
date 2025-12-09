package com.ktdsuniversity.edu.domain.chat.service;


import java.util.List;
import java.util.Map;

import com.ktdsuniversity.edu.domain.campaign.vo.CampaignVO;
import com.ktdsuniversity.edu.domain.chat.vo.ChatMessageVO;
import com.ktdsuniversity.edu.domain.chat.vo.ChatParticipantVO;
import com.ktdsuniversity.edu.domain.chat.vo.SearchChatVO;
import com.ktdsuniversity.edu.domain.chat.vo.request.RequestChatMessageVO;


public interface ChatService {
    
    // 채팅방 목록 조회 (페이징)
    public SearchChatVO readAllChatRoomList(SearchChatVO searchChatVO);
    
    public SearchChatVO readUnreadChatRoomList(SearchChatVO searchChatVO);
    
    // 캠페인 목록 조회 (페이징)
    public SearchChatVO readCampaignList(SearchChatVO searchChatVO, String status);
    
    // 채팅방 나가기
    public boolean leaveChatRoom(ChatParticipantVO participant);
    
    public ChatMessageVO sendMessage(RequestChatMessageVO requestChatMessageVO);
    
    public void updateMessagesAsRead(String chtRmId, String usrId);

	public CampaignVO readCampaignByChtRmId(String chtRmId);
	
	public Map<String, Object> readChatMessageListPaged(String chtRmId, String usrId, int page, int size);

	public List<String> readAllChtRmIdByUsrIdOrCmpnId(Map<String, String> parameter);

}
