/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignPermissionService）
 * 文件名：	AssignPermissionService.java
 * 描述：	AssignPermissionService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysAssignUser;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface AssignPermissionService {

    /**
     * Check user role assigned
     * @param userId
     * @return
     */
    boolean checkUserAssignRole(Long userId);

    /**
     * Assign user role and data range
     * @param userId
     * @param roleIdList
     * @param dataRangeCategory
     * @param selectedDataGroupId
     * @return
     */
    boolean userAssignRoleAndDataRange(Long userId, List<Long> roleIdList, String dataRangeCategory, Long selectedDataGroupId);


    /**
     * Check user group role assigned
     * @param userGroupId
     * @return
     */
    boolean checkUserGroupAssignRole(Long userGroupId);
    /**
     * Assign role and datagroup for user group
     * @param userGroupId
     * @param roleIdList
     * @param dataRangeCategory
     * @param selectedDataGroupId
     * @return
     */
    boolean userGroupAssignRoleAndDataRange(Long userGroupId, List<Long> roleIdList, String dataRangeCategory, Long selectedDataGroupId);

    /**
     * Get all roles
     * @return
     */
    List<SysRole> roleGetAll();

    /**
     * Get all UserGroup
     * @return
     */
    List<SysUserGroup> userGroupGetAll();

    /**
     * Get paginated user list by filter
     * @param userName
     * @param orgId
     * @param roleName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysAssignUser> userGetByFilterAndPage(String sortBy, String order, String userName, Long orgId, String roleName,
                                               String dataRangeCategory, Integer currentPage, Integer perPage);

    /**
     * Get user list by filter
     * @param userName
     * @param orgId
     * @param roleName
     * @return
     */
    List<SysAssignUser> userGetByFilter(String sortBy, String order, String userName, Long orgId, String roleName, String dataRangeCategory);

    /**
     * Get paginated user group by filter and page
     * @param groupName
     * @param userName
     * @param roleName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysUserGroup> userGroupGetByFilterAndPage(String sortBy, String order, String groupName, String userName, String roleName, String dataRangeCategory,
                                                         Integer currentPage, Integer perPage);

    /**
     * Get user group by filter
     * @param groupName
     * @param userName
     * @param roleName
     * @return
     */
    List<SysUserGroup> userGroupGetByFilter(String sortBy, String order, String groupName, String userName, String roleName, String dataRangeCategory);

}
