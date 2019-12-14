package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface AssignPermissionService {

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
    PageResult<SysUser> userGetByFilterAndPage(String userName, Long orgId, String roleName, Integer currentPage, Integer perPage);

    /**
     * Get user list by filter
     * @param userName
     * @param orgId
     * @param roleName
     * @return
     */
    List<SysUser> userGetByFilter(String userName, Long orgId, String roleName);

    /**
     * Get paginated user group by filter and page
     * @param groupName
     * @param userName
     * @param roleName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysUserGroup> userGroupGetByFilterAndPage(String groupName, String userName, String roleName, Integer currentPage, Integer perPage);

    /**
     * Get user group by filter
     * @param groupName
     * @param userName
     * @param roleName
     * @return
     */
    List<SysUserGroup> userGroupGetByFilter(String groupName, String userName, String roleName);

}
