package com.nuctech.ecuritycheckitem.service.permissionmanagement;

import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface OrganizationService {

    boolean createOrganization(Long parentOrgId, SysOrg sysOrg);

    boolean modifyOrganization(Long orgId, Long parentOrgId, SysOrg sysOrg);

    boolean deleteOrganization(Long orgId);

    boolean updateOrganizationStatus(Long orgId, String status);

    List<SysOrg> getAllOrganization();

    PageResult<SysOrg> getOrganizationByFilterAndPage(String orgName, String status, String parentOrgName, Integer currentPage, Integer perPage);

    List<SysOrg> getOrganizationByFilter(String orgName, String status, String parentOrgName);

}
