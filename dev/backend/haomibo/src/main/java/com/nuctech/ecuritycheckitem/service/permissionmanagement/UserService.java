package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    boolean checkUserExist(long userId);

    boolean checkOrgExist(long orgId);

    boolean checkAccountExist(String userAccount, Long userId);

    boolean checkEmailExist(String email, Long userId);

    boolean checkMobileExist(String mobile, Long userId);

    boolean checkGroupNameExist(String groupName, Long groupId);

    boolean checkGroupNumberExist(String groupNumber, Long groupId);

    boolean createUser(SysUser user, MultipartFile portraitFile);

    boolean modifyUser(SysUser user, MultipartFile portraitFile);

    PageResult<SysUser> getUserListByPage(String userName, String status, String gender, Long orgId, int currentPage, int perPage);

    List<SysUser> getExportUserListByPage(String userName, String status, String gender, Long orgId, boolean isAll, String idList);

    boolean updateStatus(long userId, String status);

    List<SysUser> findAllUser();

    boolean createUserGroup(SysUserGroup userGroup, List<Long> userIdList);

    PageResult<SysUserGroup> getUserGroupListByPage(String groupName, int currentPage, int perPage);

    List<SysUserGroup> getExportUserGroupListByPage(String groupName, boolean isAll, String idList);

    boolean checkUserGroupExist(long userGroupId);

    boolean checkUserGroupUserExist(long userGroupId);

    boolean checkUserGroupRoleExist(long userGroupId);

    boolean modifyUserGroup(long userGroupId, List<Long> userIdList);

    boolean removeUserGroup(long userGroupId);

    List<SysResource> getResourceList(long userId);
}
