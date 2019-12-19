package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface OrganizationService {

    boolean checkOrgNameExist(String orgName, Long orgId);

    boolean checkOrgNumberExist(String orgNumber, Long orgId);

    boolean checkUserExist(Long orgId);

    boolean checkRoleExist(Long orgId);

    boolean checkUserGroupExist(Long orgId);

    boolean checkDataGroupExist(Long orgId);

    boolean checkFieldExist(Long orgId);

    boolean checkChildrenExist(Long orgId);

    boolean createOrganization(Long parentOrgId, SysOrg sysOrg);

    boolean modifyOrganization(Long orgId, Long parentOrgId, SysOrg sysOrg);

    boolean deleteOrganization(Long orgId);

    boolean updateOrganizationStatus(Long orgId, String status);

    List<SysOrg> getAllOrganization(boolean isAll);

    PageResult<SysOrg> getOrganizationByFilterAndPage(String orgName, String status, String parentOrgName, Integer currentPage, Integer perPage);

    List<SysOrg> getOrganizationByFilter(String orgName, String status, String parentOrgName);

}
