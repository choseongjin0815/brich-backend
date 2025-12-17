package com.ktdsuniversity.edu.domain.campaign.vo.response;

import java.util.List;

import com.ktdsuniversity.edu.domain.file.vo.FileVO;

public class ResponseAdminAdopterPstRtrnRsnVO {

	private String postId;
	private String postRtrnRsn;
	private String crtDt;
	private String retnFlGrpId;
	private List<FileVO> fileList;
	
	private String postRtrnHistId;
	
	public String getPostId() {
		return this.postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getPostRtrnRsn() {
		return this.postRtrnRsn;
	}
	public void setPostRtrnRsn(String postRtrnRsn) {
		this.postRtrnRsn = postRtrnRsn;
	}
	public String getCrtDt() {
		return this.crtDt;
	}
	public void setCrtDt(String crtDt) {
		this.crtDt = crtDt;
	}
	public String getRetnFlGrpId() {
		return this.retnFlGrpId;
	}
	public void setRetnFlGrpId(String retnFlGrpId) {
		this.retnFlGrpId = retnFlGrpId;
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
		return "ResponseAdminAdopterPstRtrnRsnVO [postId=" + postId + ", postRtrnRsn=" + postRtrnRsn + ", crtDt="
				+ crtDt + ", retnFlGrpId=" + retnFlGrpId + ", fileList=" + fileList + ", postRtrnHistId=" + postRtrnHistId
				+ "]";
	}
	
}
