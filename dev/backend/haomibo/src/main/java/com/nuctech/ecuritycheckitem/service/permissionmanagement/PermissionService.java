/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PermissionService）
 * 文件名：	PermissionService.java
 * 描述：	PermissionService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface PermissionService {

    /**
     * create new role
     * @param role
     * @param resourceIdList
     * @return
     */
    boolean createRole(SysRole role, List<Long> resourceIdList);

    /**
     * get paginated and filtered role list
     * @param roleName
     * @param resourceName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysRole> getRoleListByPage(String sortBy, String order, String roleName, String resourceName, int currentPage, int perPage, String locale);

    /**
     * get export role list
     * @param roleName
     * @param resourceName
     * @param isAll
     * @param idList
     * @return
     */
    List<SysRole> getExportListByFilter(String sortBy, String order, String roleName, String resourceName, boolean isAll, String idList, String locale);

    /**
     * check if role exists
     * @param roleId
     * @return
     */
    boolean checkRoleExist(long roleId);

    /**
     * edit role
     * @param roleId
     * @param resourceIdList
     * @param roleName
     * @return
     */
    boolean modifyRole(long roleId, String roleName, List<Long> resourceIdList);

    /**
     * check if resource exists
     * @param roleId
     * @return
     */
    boolean checkResourceExist(long roleId);

    /**
     * check if user exists
     * @param roleId
     * @return
     */
    boolean checkUserExist(long roleId);

    /**
     * check if usergroup exists
     * @param roleId
     * @return
     */
    boolean checkUserGroupExist(long roleId);

    /**
     * check if group name exists
     * @param groupName
     * @param groupId
     * @return
     */
    boolean checkGroupNameExist(String groupName, Long groupId);

    /**
     * check if group exists
     * @param groupNumber
     * @return
     */
    boolean checkGroupNumberExist(String groupNumber);

    /**
     * check if rolename exists
     * @param roleName
     * @param roleId
     * @return
     */
    boolean checkRoleNameExist(String roleName, Long roleId);

    /**
     * check if rolenumber exists
     * @param roleNumber
     * @param roleId
     * @return
     */
    boolean checkRoleNumberExist(String roleNumber, Long roleId);

    /**
     * remove role
     * @param roleId
     */
    void removeRole(long roleId);

    /**
     * create datagroup
     * @param dataGroup
     * @param userIdList
     * @return
     */
    boolean createDataGroup(SysDataGroup dataGroup, List<Long> userIdList);

    /**
     * get pagniated and filtered data group
     * @param dataGroupName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysDataGroup> getDataGroupListByPage(String sortBy, String order, String dataGroupName, String userName, int currentPage, int perPage);

    /**
     * get datagroup export list
     * @param dataGroupName
     * @param isAll
     * @param idList
     * @return
     */
    List<SysDataGroup> getExportGroupListByFilter(String sortBy, String order, String dataGroupName, String userName, boolean isAll, String idList);

    /**
     * find all resources
     * @return
     */
    List<SysResource> findAllResource();

    /**
     * find all datagroup
     * @return
     */
    List<SysDataGroup> findAllDataGroup();

    /**
     * edit data group
     * @param dataGroupId
     * @param dataGroupName
     * @param userIdList
     * @return
     */
    boolean modifyDataGroup(long dataGroupId, String dataGroupName, List<Long> userIdList);

    /**
     * check if datagroup exists
     * @param dataGroupId
     * @return
     */
    boolean checkDataGroupExist(long dataGroupId);

    /**
     * check if datagroup children exists
     * @param dataGroupId
     * @return
     */
    boolean checkGroupChildExist(long dataGroupId);

    /**
     * check if userloopup exists
     * @param dataGroupId
     * @return
     */
    boolean checkUserLookUpExist(long dataGroupId);

    /**
     * check if datagrouploopup exists
     * @param dataGroupId
     * @return
     */
    boolean checkDataGroupLookupExist(long dataGroupId);

    /**
     * remove datagroup
     * @param dataGroupId
     * @return
     */
    boolean removeDataGroup(long dataGroupId);


}
