/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（OrganizationServiceImpl）
 * 文件名：	OrganizationServiceImpl.java
 * 描述：	OrganizationService impl
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysFieldRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.OrganizationService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    SysOrgRepository sysOrgRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    SysFieldRepository sysFieldRepository;

    @Autowired
    SysUserGroupRepository sysUserGroupRepository;

    @Autowired
    SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromOrg(SysOrg org) {
        SysOrg newOrg = SysOrg.builder()
                .orgId(org.getOrgId())
                .parentOrgId(org.getParentOrgId())
                .orgName(org.getOrgName())
                .orgNumber(org.getOrgNumber())
                .leader(org.getLeader())
                .mobile(org.getMobile())
                .status(org.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newOrg);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * check if org name exists
     * @param orgName
     * @param orgId
     * @return
     */
    @Override
    public boolean checkOrgNameExist(String orgName, Long orgId) {
        if (orgId == null) {
            return sysOrgRepository.exists(QSysOrg.sysOrg.orgName.eq(orgName));
        }
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgName.eq(orgName)
                .and(QSysOrg.sysOrg.orgId.ne(orgId)));
    }

    /**
     * check if org number exists
     * @param orgNumber
     * @param orgId
     * @return
     */
    @Override
    public boolean checkOrgNumberExist(String orgNumber, Long orgId) {
        if (orgId == null) {
            return sysOrgRepository.exists(QSysOrg.sysOrg.orgNumber.eq(orgNumber));
        }
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgNumber.eq(orgNumber)
                .and(QSysOrg.sysOrg.orgId.ne(orgId)));
    }

    /**
     * check if user exists whose org is specified orgid
     * @param orgId
     * @return
     */
    @Override
    public boolean checkUserExist(Long orgId) {
        return sysUserRepository.exists(QSysUser.sysUser.orgId.eq(orgId));
    }

    /**
     * check if role exists whose org id is specified id
     * @param orgId
     * @return
     */
    @Override
    public boolean checkRoleExist(Long orgId) {
        return sysRoleRepository.exists(QSysRole.sysRole.orgId.eq(orgId));
    }

    /**
     * check if usergroup whose org id is specified id exists
     * @param orgId
     * @return
     */
    @Override
    public boolean checkUserGroupExist(Long orgId) {
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.orgId.eq(orgId));
    }

    /**
     * check if datagroup exists whose org id is specified org id
     * @param orgId
     * @return
     */
    @Override
    public boolean checkDataGroupExist(Long orgId) {
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.orgId.eq(orgId));
    }

    /**
     * check if field whose org id is specified id exists
     * @param orgId
     * @return
     */
    @Override
    public boolean checkFieldExist(Long orgId) {
        return sysFieldRepository.exists(QSysField.sysField.orgId.eq(orgId));
    }

    /**
     * check if children exist
     * @param orgId
     * @return
     */
    @Override
    public boolean checkChildrenExist(Long orgId) {
        return sysOrgRepository.exists(QSysOrg.sysOrg.parentOrgId.eq(orgId));
    }

    /**
     * create new organization
     * @param parentOrgId
     * @param sysOrg
     * @return
     */
    public boolean createOrganization(Long parentOrgId, SysOrg sysOrg) {

        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(parentOrgId))) {
            return false;
        }

        // Add createdInfo.
        sysOrg.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);
        String valueAfter = getJsonFromOrg(sysOrg);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Org", null, currentLocale), "", sysOrg.getOrgId().toString(), null, true, "", valueAfter);

        return true;

    }

    /**
     * modify organization
     * @param orgId
     * @param parentOrgId
     * @param sysOrg
     * @return
     */
    public boolean modifyOrganization(Long orgId, Long parentOrgId, SysOrg sysOrg) {
        SysOrg oldSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId)).orElse(null);
        String valueBefore = getJsonFromOrg(oldSysOrg);
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
        sysOrg.setStatus(oldSysOrg.getStatus());

        // Add edited info.
        sysOrg.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);
        String valueAfter = getJsonFromOrg(sysOrg);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Org", null, currentLocale), "", sysOrg.getOrgId().toString(), null, true, valueBefore, valueAfter);


        return true;
    }

    /**
     * remove organization
     * @param orgId
     * @return
     */
    public boolean deleteOrganization(Long orgId) {
        SysOrg oldSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId)).orElse(null);
        if(oldSysOrg.getStatus().equals(SysOrg.Status.INACTIVE)) {
            String valueBefore = getJsonFromOrg(oldSysOrg);

            sysOrgRepository.delete(SysOrg.builder().orgId(orgId).build());
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale), "", String.valueOf(orgId), null, true, valueBefore, "");
            return true;
        }
        return false;

    }

    /**
     * update organization status
     * @param orgId
     * @param status
     * @return
     */
    public boolean updateOrganizationStatus(Long orgId, String status) {
        // Check if org is existing.
        Optional<SysOrg> optionalSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId));
        if (!optionalSysOrg.isPresent()) {
            return false;
        }

        SysOrg sysOrg = optionalSysOrg.get();
        String valueBefore = getJsonFromOrg(sysOrg);
        // Update status.
        sysOrg.setStatus(status);

        // Add edited info.
        sysOrg.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysOrgRepository.save(sysOrg);
        String valueAfter = getJsonFromOrg(sysOrg);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Org", null, currentLocale), "", String.valueOf(orgId), null, true, valueBefore, valueAfter);
        return true;
    }

    /**
     * get all organizations
     * @param isAll
     * @return
     */
    public List<SysOrg> getAllOrganization(boolean isAll) {
        QSysOrg builder = QSysOrg.sysOrg;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (isAll == false) {
            predicate.and(builder.status.eq(SysOrg.Status.ACTIVE));
        }

        return StreamSupport
                .stream(sysOrgRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get paginated and filered organizations list
     * @param orgName
     * @param status
     * @param parentOrgName
     * @param currentPage
     * @param perPage
     * @return
     */
    public PageResult<SysOrg> getOrganizationByFilterAndPage(String sortBy, String order, String orgName, String status, String parentOrgName, Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getPredicate(orgName, status, parentOrgName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }

        long total = sysOrgRepository.count(predicate);
        List<SysOrg> data = sysOrgRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SysOrg>(total, data);

    }

    /**
     * get filtered organization list
     * @param orgName
     * @param status
     * @param parentOrgName
     * @return
     */
    public List<SysOrg> getOrganizationByFilter(String sortBy, String order, String orgName, String status, String parentOrgName) {

        BooleanBuilder predicate = getPredicate(orgName, status, parentOrgName);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        if(sort != null) {
            return StreamSupport
                    .stream(sysOrgRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return StreamSupport
                .stream(sysOrgRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * get predicate from filter parameters
     * @param orgName
     * @param status
     * @param parentOrgName
     * @return
     */
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
