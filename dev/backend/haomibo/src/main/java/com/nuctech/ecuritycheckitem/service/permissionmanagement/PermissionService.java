package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface PermissionService {
    boolean createRole(SysRole role, List<Long> resourceIdList);

    PageResult<SysRole> getRoleListByPage(String roleName, int currentPage, int perPage);

    List<SysRole> getExportListByFilter(String roleName, boolean isAll, String idList);

    boolean checkRoleExist(long roleId);

    boolean modifyRole(long roleId, List<Long> resourceIdList);

    boolean checkResourceExist(long roleId);

    boolean checkUserExist(long roleId);

    boolean checkUserGroupExist(long roleId);

    boolean checkGroupNameExist(String groupName);

    boolean checkGroupNumberExist(String groupNumber);

    boolean checkRoleNameExist(String roleName, Long roleId);

    boolean checkRoleNumberExist(String roleNumber, Long roleId);

    void removeRole(long roleId);

    boolean createDataGroup(SysDataGroup dataGroup, List<Long> userIdList);

    PageResult<SysDataGroup> getDataGroupListByPage(String dataGroupName, int currentPage, int perPage);

    List<SysDataGroup> getExportGroupListByFilter(String dataGroupName, boolean isAll, String idList);

    List<SysResource> findAllResource();

    List<SysDataGroup> findAllDataGroup();

    boolean modifyDataGroup(long dataGroupId, List<Long> userIdList);

    boolean checkDataGroupExist(long dataGroupId);

    boolean checkGroupChildExist(long dataGroupId);

    boolean checkUserLookUpExist(long dataGroupId);

    boolean checkDataGroupLookupExist(long dataGroupId);

    boolean removeDataGroup(long dataGroupId);


}
