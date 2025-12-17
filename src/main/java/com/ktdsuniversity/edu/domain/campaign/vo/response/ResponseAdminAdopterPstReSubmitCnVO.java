package com.ktdsuniversity.edu.domain.campaign.vo.response;

import java.util.List;

import com.ktdsuniversity.edu.domain.file.vo.FileVO;

public class ResponseAdminAdopterPstReSubmitCnVO {

	private String postId;
	private String postSubmitChgCn;
	private String crtDt;
	private String submitFlGrpId;
	private List<FileVO> fileList;
	
	private String postRtrnHistId;
	
	public String getPostId() {
		return this.postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getPostSubmitChgCn() {
		return this.postSubmitChgCn;
	}
	public void setPostSubmitChgCn(String postSubmitChgCn) {
		this.postSubmitChgCn = postSubmitChgCn;
	}
	public String getCrtDt() {
		return this.crtDt;
	}
	public void setCrtDt(String crtDt) {
		this.crtDt = crtDt;
	}
	public String getSubmitFlGrpId() {
		return this.submitFlGrpId;
	}
	public void setSubmitFlGrpId(String submitFlGrpId) {
		this.submitFlGrpId = submitFlGrpId;
	}
	public List<FileVO> getFileList() {
		return this.fileList;
	}
	public void setFileList(List<FileVO> fileList) {
		this.fileList = fileList;
	}
	public String getPostRtrnHistId() {
		return postRtrnHistId;
	}
	public void setPostRtrnHistId(String postRtrnHistId) {
		this.postRtrnHistId = postRtrnHistId;
	}
	
	@Override
	public String toString() {
		return "ResponseAdminAdopterPstReSubmitCnVO [postId=" + postId + ", postSubmitChgCn=" + postSubmitChgCn
				+ ", crtDt=" + crtDt + ", submitFlGrpId=" + submitFlGrpId + ", fileList=" + fileList + ", postRtrnHistId="
				+ postRtrnHistId + "]";
	}
	
}
