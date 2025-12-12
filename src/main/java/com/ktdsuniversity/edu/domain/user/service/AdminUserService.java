package com.ktdsuniversity.edu.domain.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ktdsuniversity.edu.domain.report.vo.AdminPenaltyRequestVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserBaseInfoVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserListVO;
import com.ktdsuniversity.edu.domain.user.vo.AdminUserModifyInfoVO;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;

public interface AdminUserService {
	
	/* ======================================== 1차 ======================================== */

	AdminUserBaseInfoVO readAdminUserDetailById(String usrId);

	boolean updateAdvertiserRegistAuthCode(Map<String, String> requestData);

	List<AdminUserListVO> readAdminUserList(String tab);

	List<CommonCodeVO> readBlogCategoryList();

	boolean updateBlogAddress(AdminUserModifyInfoVO adminUserModifyInfoVO);

	boolean updateUserPenaltyInfo(AdminPenaltyRequestVO adminPanaltyRequestVO);

	/* ======================================== 2차 ======================================== */
	
	List<AdminUserListVO> readAdminAllUserList();

	List<AdminUserListVO> readAdminBloggerList();

	List<AdminUserListVO> readAdminAdvertiserList();

	boolean updateAdvertiserRegistApprove(String usrId, String adminId);

	boolean updateAdvertiserRegistReject(String usrId, String adminId);
	
	boolean updateUserInfo(AdminUserModifyInfoVO adminUserModifyInfoVO, List<MultipartFile> newFiles, String fileToDeleteJson);

}
