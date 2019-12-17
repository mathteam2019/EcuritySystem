package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.QSysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.OrganizationService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    SysOrgRepository sysOrgRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public boolean checkOrgNameExist(String orgName, Long orgId) {
        if(orgId == null) {
            return sysOrgRepository.exists(QSysOrg.sysOrg.orgName.eq(orgName));
        }
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgName.eq(orgName)
                .and(QSysOrg.sysOrg.orgId.ne(orgId)));
    }

    @Override
    public boolean checkOrgNumberExist(String orgNumber, Long orgId) {
        if(orgId == null) {
            return sysOrgRepository.exists(QSysOrg.sysOrg.orgNumber.eq(orgNumber));
        }
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgNumber.eq(orgNumber)
                .and(QSysOrg.sysOrg.orgId.ne(orgId)));
    }

    public boolean createOrganization(Long parentOrgId, SysOrg sysOrg) {

        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(parentOrgId))) {
            return false;
        }

        // Add createdInfo.
        sysOrg.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);

        return true;

    }

    public boolean modifyOrganization(Long orgId, Long parentOrgId, SysOrg sysOrg) {
        SysOrg oldSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId)).orElse(null);

        // Check if org is existing.
        if (oldSysOrg == null) {
            return false;
        }

        // Check if parent org is existing.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(parentOrgId))) {
            return false;
        }

        //Don't modify created by and created time
        sysOrg.setCreatedBy(oldSysOrg.getCreatedBy());
        sysOrg.setCreatedTime(oldSysOrg.getCreatedTime());

        // Add edited info.
        sysOrg.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);

        return true;
    }

    public boolean deleteOrganization(Long orgId) {

        boolean isHavingChildren = sysOrgRepository.exists(QSysOrg.sysOrg.parentOrgId.eq(orgId));
        if (isHavingChildren) {
            // Can't delete if org has children.
            return false;
        }

        sysOrgRepository.delete(SysOrg.builder().orgId(orgId).build());

        return true;
    }

    public boolean updateOrganizationStatus(Long orgId, String status) {
        // Check if org is existing.
        Optional<SysOrg> optionalSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId));
        if (!optionalSysOrg.isPresent()) {
            return false;
        }

        SysOrg sysOrg = optionalSysOrg.get();

        // Update status.
        sysOrg.setStatus(status);

        // Add edited info.
        sysOrg.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);

        return true;
    }

    public List<SysOrg> getAllOrganization() {
        return sysOrgRepository.findAll();
    }

    public PageResult<SysOrg> getOrganizationByFilterAndPage(String orgName, String status, String parentOrgName, Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getPredicate(orgName, status, parentOrgName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysOrgRepository.count(predicate);
        List<SysOrg> data = sysOrgRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SysOrg>(total, data);

    }

    public List<SysOrg> getOrganizationByFilter(String orgName, String status, String parentOrgName) {

        BooleanBuilder predicate = getPredicate(orgName, status, parentOrgName);
        return StreamSupport
                .stream(sysOrgRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

    }

    private BooleanBuilder getPredicate(String orgName, String status, String parentOrgName) {
        QSysOrg builder = QSysOrg.sysOrg;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(orgName)) {
            predicate.and(builder.orgName.contains(orgName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (!StringUtils.isEmpty(parentOrgName)) {
            predicate.and(builder.parent.orgName.contains(parentOrgName));
        }

        return predicate;
    }
}
