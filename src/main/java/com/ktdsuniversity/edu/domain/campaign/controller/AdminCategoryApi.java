package com.ktdsuniversity.edu.domain.campaign.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.domain.campaign.service.AdminCategoryService;
import com.ktdsuniversity.edu.domain.campaign.vo.AdminCampaignCategoryVO;
import com.ktdsuniversity.edu.global.common.AjaxResponse;

/**
 * 캠페인 카테고리 관리 (관리자 전용)
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/v1/campaign")
public class AdminCategoryApi {

	private static final Logger log = LoggerFactory.getLogger(AdminCategoryApi.class);

	@Autowired
	private AdminCategoryService adminCategoryService;

	/**
	 * 카테고리 목록 조회 (기존: viewAdminCategoryManagePage에서 model에 담던 데이터)
	 * - 프론트(React/JSP/관리페이지)가 이 API로 목록 받아서 화면 렌더링
	 */
	@GetMapping("/admin/categories")
	public AjaxResponse getCampaignCategoryList() {

		List<AdminCampaignCategoryVO> campaignCategoryList = this.adminCategoryService.readCampaignCategoryList();

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(campaignCategoryList);

		return ajaxResponse;
	}

	/**
	 * 카테고리 분할 option List 받아오기 (자신의 하위 카테고리)
	 */
	@GetMapping("/admin/categories/{parentCdId}/children")
	public AjaxResponse getChildrenCategoryList(@PathVariable String parentCdId) {

		List<AdminCampaignCategoryVO> divTargetCategory = this.adminCategoryService.readChildrenCategoryList(parentCdId);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(divTargetCategory);

		return ajaxResponse;
	}

	/**
	 * 카테고리 병합 option List 받아오기 (자신 제외 상위 카테고리)
	 * - excludeCdId는 query param으로 받거나 path로 빼도 되는데, 일단 path + query 둘 다 가능하게 유지 가능
	 */
	@GetMapping("/admin/categories/parents")
	public AjaxResponse getParentCategoryList(@RequestParam String excludeCdId) {

		List<AdminCampaignCategoryVO> mergeTargetCategory = this.adminCategoryService.readParentCategoryList(excludeCdId);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(mergeTargetCategory);

		return ajaxResponse;
	}

	/**
	 * 카테고리 추가 (SELECT/INSERT)
	 */
	@PostMapping("/admin/categories")
	public AjaxResponse addCampaignCategory(@RequestBody AdminCampaignCategoryVO adminCampaignCategoryVO) {

		boolean isSuccess = this.adminCategoryService.createNewCampaignCategory(adminCampaignCategoryVO);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);

		return ajaxResponse;
	}

	/**
	 * 카테고리 분할 (UPDATE)
	 */
	@PutMapping("/admin/categories/div")
	public AjaxResponse divCampaignCategory(@RequestBody AdminCampaignCategoryVO adminCampaignCategoryVO) {

		boolean isSuccess = this.adminCategoryService.updateDivCampaignCategory(adminCampaignCategoryVO);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);

		return ajaxResponse;
	}

	/**
	 * 카테고리 병합 (UPDATE)
	 */
	@PutMapping("/admin/categories/merge")
	public AjaxResponse mergeCampaignCategory(@RequestBody AdminCampaignCategoryVO adminCampaignCategoryVO) {

		boolean isSuccess = this.adminCategoryService.updateMergeCampaignCategory(adminCampaignCategoryVO);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);

		return ajaxResponse;
	}

	/**
	 * 카테고리 노출 순서 변경 (UPDATE)
	 */
	@PutMapping("/admin/categories/order")
	public AjaxResponse changeOrderCampaignCategory(@RequestBody AdminCampaignCategoryVO adminCampaignCategoryVO) {

		boolean isSuccess = this.adminCategoryService.updateChangeOrderCampaignCategory(adminCampaignCategoryVO);

		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setBody(isSuccess);

		return ajaxResponse;
	}
}
