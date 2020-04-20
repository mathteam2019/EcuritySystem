/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserService）
 * 文件名：	UserService.java
 * 描述：	UserService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysUserSimplifiedOnlyHasName;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    /**
     * check if user exists
     * @param userId
     * @return
     */
    boolean checkUserExist(long userId);

    /**
     * check if org exists
     * @param orgId
     * @return
     */
    boolean checkOrgExist(long orgId);

    /**
     * check if account exists
     * @param userAccount
     * @param userId
     * @return
     */
    boolean checkAccountExist(String userAccount, Long userId);

    /**
     * check if account exists
     * @param userNumber
     * @param userId
     * @return
     */
    boolean checkNumberExist(String userNumber, Long userId);

    /**
     * check if  email exists
     * @param email
     * @param userId
     * @return
     */
    boolean checkEmailExist(String email, Long userId);

    /**
     * check if mobile exists
     * @param mobile
     * @param userId
     * @return
     */
    boolean checkMobileExist(String mobile, Long userId);

    /**
     * Check user group exist which contain user
     * @param userId
     * @return
     */
    boolean checkParentUserGroupExist(Long userId);

    /**
     * Check data group exist which contain user
     * @param userId
     * @return
     */
    boolean checkParentDataGroupExist(Long userId);

    /**
     * Check user have role
     * @param userId
     * @return
     */
    boolean checkRoleExist(Long userId);



    /**
     * check if groupname exists
     * @param groupName
     * @param groupId
     * @return
     */
    boolean checkGroupNameExist(String groupName, Long groupId);

    /**
     * check if groupnumber exists
     * @param groupNumber
     * @param groupId
     * @return
     */
    boolean checkGroupNumberExist(String groupNumber, Long groupId);

    /**
     * create new user
     * @param user
     * @param portraitFile
     * @return
     */
    boolean createUser(SysUser user, MultipartFile portraitFile);

    /**
     * edit user
     * @param user
     * @param portraitFile
     * @return
     */
    boolean modifyUser(SysUser user, MultipartFile portraitFile);

    /**
     * get paginated and filtered user list
     * @param userName
     * @param status
     * @param gender
     * @param orgId
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysUser> getUserListByPage(String sortBy, String order, String userName, String status, String gender, Long orgId, int currentPage, int perPage);

    /**
     * get export user list
     * @param userName
     * @param status
     * @param gender
     * @param orgId
     * @param isAll
     * @param idList
     * @return
     */
    List<SysUser> getExportUserListByPage(String sortBy, String order, String userName, String status, String gender, Long orgId, boolean isAll, String idList);

    /**
     * update user status
     * @param userId
     * @param status
     * @return
     */
    boolean updateStatus(long userId, String status);

    /**
     * modify user password
     * @param userId
     * @param password
     * @return
     */
    boolean modifyPassword(long userId, String password);

    /**
     * find all user
     * @return
     */
    List<SysUserSimplifiedOnlyHasName> findAllUser();

    /**
     * create user group
     * @param userGroup
     * @param userIdList
     * @return
     */
    boolean createUserGroup(SysUserGroup userGroup, List<Long> userIdList);

    /**
     * get paginated and fitlered user group list
     * @param groupName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysUserGroup> getUserGroupListByPage(String sortBy, String order, String groupName, String userName, int currentPage, int perPage);

    /**
     * get export usergroup list
     * @param groupName
     * @param isAll
     * @param idList
     * @return
     */
    List<SysUserGroup> getExportUserGroupListByPage(String sortBy, String order, String groupName, String userName, boolean isAll, String idList);

    /**
     * check if usergroup exists
     * @param userGroupId
     * @return
     */
    boolean checkUserGroupExist(long userGroupId);

    /**
     * check if userGroup's user exists
     * @param userGroupId
     * @return
     */
    boolean checkUserGroupUserExist(long userGroupId);

    /**
     * check if UserGroup's role exists
     * @param userGroupId
     * @return
     */
    boolean checkUserGroupRoleExist(long userGroupId);

    /**
     * edit user group role
     * @param userGroupId
     * @param userIdList
     * @param groupName
     * @return
     */
    boolean modifyUserGroup(long userGroupId, String groupName, List<Long> userIdList);

    /**
     * remove user group
     * @param userGroupId
     * @return
     */
    boolean removeUserGroup(long userGroupId);

    /**
     * get resource list
     * @param userId
     * @return
     */
    List<SysResource> getResourceList(long userId);


    /**
     * get User List by Resource
     * @param resourceId
     * @return
     */
    List<Long> getUserListByResource(Long resourceId);
}
