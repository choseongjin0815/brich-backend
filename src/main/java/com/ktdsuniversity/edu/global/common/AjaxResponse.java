package com.ktdsuniversity.edu.global.common;

import java.util.Map;

public class AjaxResponse {

	private Object body;	// 정상 처리 결과
	private AbstractSearchVO paginator;
	private Map<String, Object> error;	// 예외가 있을 때
	
	private int nowPage;
	private boolean done;	// 다음 페이지가 있는지 여부

	public Object getBody() {
		return this.body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	public AbstractSearchVO getPaginator() {
		return this.paginator;
	}
	public void setPaginator(AbstractSearchVO paginator) {
		this.paginator = paginator;
	}
	public Map<String, Object> getError() {
		return this.error;
	}

	public void setError(Map<String, Object> error) {
		this.error = error;
	}

	public int getNowPage() {
		return this.nowPage;
	}

	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}

	public boolean isDone() {
		return this.done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}


	
}
