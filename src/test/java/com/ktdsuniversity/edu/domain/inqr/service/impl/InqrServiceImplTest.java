package com.ktdsuniversity.edu.domain.inqr.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ktdsuniversity.edu.domain.inqr.dao.InqrDao;
import com.ktdsuniversity.edu.domain.inqr.dao.impl.InqrDaoImpl;
import com.ktdsuniversity.edu.domain.inqr.service.InqrService;
import com.ktdsuniversity.edu.domain.inqr.vo.request.RequestInqrCreateVO;
import com.ktdsuniversity.edu.domain.inqr.vo.response.ResponseInqrVO;
import com.ktdsuniversity.edu.global.exceptions.AjaxException;

@SpringBootTest
@Import({ InqrServiceImpl.class, InqrDaoImpl.class })
public class InqrServiceImplTest {

	@Autowired
	private InqrService inqrService;

	@MockitoBean
	private InqrDao inqrDao;

	@Test
	@DisplayName("문의글 조회 성공 테스트")
	public void readInqrDetailByInqrIdSuccessTest() {
		ResponseInqrVO inqrVO = new ResponseInqrVO();
		inqrVO.setInqrTitle("test");
		// given
		BDDMockito.given(this.inqrDao.selectInqrDetailByInqrId("INQR-20252011")).willReturn(inqrVO);
		// when
		ResponseInqrVO inqrTestVO = this.inqrService.readInqrDetailByInqrId("INQR-20252011");
		// then
		assertTrue(inqrVO.getInqrTitle().equals(inqrTestVO.getInqrTitle()));
		Mockito.verify(this.inqrDao).selectInqrDetailByInqrId("INQR-20252011");

	}

	@Test
	@DisplayName("문의글 조회 실패 테스트 - 존재하지 않는 문의글")
	public void readInqrDetailByInqrIdFailTest() {

		// given
		BDDMockito.given(this.inqrDao.selectInqrDetailByInqrId("INVALID_ID")).willReturn(null);

		// when & then
		assertThrows(AjaxException.class, () -> this.inqrService.readInqrDetailByInqrId("INVALID_ID"));

		Mockito.verify(this.inqrDao).selectInqrDetailByInqrId("INVALID_ID");
	}

	@Test
	@DisplayName("문의글 생성 성공 테스트")
	public void createInqrSuccessTest() {
		// given
		RequestInqrCreateVO inqrVO = new RequestInqrCreateVO();
		BDDMockito.given(this.inqrDao.insertInqr(inqrVO)).willReturn(1);

		// when
		boolean createResult = this.inqrService.createNewInqr(inqrVO);

		// then
		assertTrue(createResult);
		Mockito.verify(this.inqrDao).insertInqr(inqrVO);

	}

	@Test
	@DisplayName("문의글 생성 실패 테스트 - 입력 데이터가 null")
	public void createInqrFailTest() {
		// given
		// (입력값 검증이 목적이므로 불필요)
		// when & then
		assertThrows(AjaxException.class, () -> this.inqrService.createNewInqr(null));
		// DAO 호출이 없었는지 검증
		Mockito.verifyNoInteractions(this.inqrDao);
	}

}
