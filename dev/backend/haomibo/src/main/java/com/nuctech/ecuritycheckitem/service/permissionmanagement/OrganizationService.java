/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（OrganizationService）
 * 文件名：	OrganizationService.java
 * 描述：	OrganizationService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface OrganizationService {

    /**
     * check if org name exists
     * @param orgName
     * @param orgId
     * @return
     */
    boolean checkOrgNameExist(String orgName, Long orgId);

    /**
     * check if org number exists
     * @param orgNumber
     * @param orgId
     * @return
     */
    boolean checkOrgNumberExist(String orgNumber, Long orgId);

    /**
     * check if user exists whose org id is specified id
     * @param orgId
     * @return
     */
    boolean checkUserExist(Long orgId);

    /**
     * check if role exists whose org id is specified org id
     * @param orgId
     * @return
     */
    boolean checkRoleExist(Long orgId);

    /**
     * check if user group exists  whose org id is specified org id
     * @param orgId
     * @return
     */
    boolean checkUserGroupExist(Long orgId);

    /**
     * check if datagroup exists  whose org id is specified org id
     * @param orgId
     * @return
     */
    boolean checkDataGroupExist(Long orgId);

    /**
     * check if field exists whose org id is specified org id
     * @param orgId
     * @return
     */
    boolean checkFieldExist(Long orgId);

    /**
     * check if children exists
     * @param orgId
     * @return
     */
    boolean checkChildrenExist(Long orgId);

    /**
     * create new organization
     * @param parentOrgId
     * @param sysOrg
     * @return
     */
    boolean createOrganization(Long parentOrgId, SysOrg sysOrg);

    /**
     * edit organization
     * @param orgId
     * @param parentOrgId
     * @param sysOrg
     * @return
     */
    boolean modifyOrganization(Long orgId, Long parentOrgId, SysOrg sysOrg);

    /**
     * remove organization
     * @param orgId
     * @return
     */
    boolean deleteOrganization(Long orgId);

    /**
     * update organization status
     * @param orgId
     * @param status
     * @return
     */
    boolean updateOrganizationStatus(Long orgId, String status);

    /**
     * get all organizations
     * @param isAll
     * @return
     */
    List<SysOrg> getAllOrganization(boolean isAll);

    /**
     * get filtered and paginated organization list
     * @param orgName
     * @param status
     * @param parentOrgName
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysOrg> getOrganizationByFilterAndPage(String orgName, String status, String parentOrgName, Integer currentPage, Integer perPage);

    /**
     * get filtered organization list
     * @param orgName
     * @param status
     * @param parentOrgName
     * @return
     */
    List<SysOrg> getOrganizationByFilter(String orgName, String status, String parentOrgName);

}
