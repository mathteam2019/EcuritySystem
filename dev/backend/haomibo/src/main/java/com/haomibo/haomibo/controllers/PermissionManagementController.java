package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.jwt.JwtUtil;
import com.haomibo.haomibo.models.db.QSysOrg;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.request.DeleteOrgRequestBody;
import com.haomibo.haomibo.models.request.ModifyOrgRequestBody;
import com.haomibo.haomibo.models.request.NewOrgRequestBody;
import com.haomibo.haomibo.models.request.UpdateOrgStatusRequestBody;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysOrgRepository;
import com.haomibo.haomibo.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/permission-management")
public class PermissionManagementController extends BaseController {

    @Autowired
    SysOrgRepository sysOrgRepository;

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/new-organization", method = RequestMethod.POST)
    public Object newOrg(
            @RequestBody @Valid NewOrgRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        boolean isParentOrgExisting = sysOrgRepository.count(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId())) > 0;
        if (!isParentOrgExisting) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/modify-organization", method = RequestMethod.POST)
    public Object modifyOrg(
            @RequestBody @Valid ModifyOrgRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        boolean isOrgExisting = sysOrgRepository.count(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId())) > 0;
        if (!isOrgExisting) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        boolean isParentOrgExisting = sysOrgRepository.count(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId())) > 0;
        if (!isParentOrgExisting) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/delete-organization", method = RequestMethod.POST)
    public Object deleteOrg(
            @RequestBody @Valid DeleteOrgRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org has children
        boolean isHavingChildren = sysOrgRepository.count(QSysOrg.sysOrg.parentOrgId.eq(requestBody.getOrgId())) > 0;
        if (isHavingChildren) {
            return new CommonResponseBody(Constants.ResponseMessages.HAS_CHILDREN);
        }

        sysOrgRepository.delete(SysOrg.builder().orgId(requestBody.getOrgId()).build());

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/update-organization-status", method = RequestMethod.POST)
    public Object updateOrgStatus(
            @RequestBody @Valid UpdateOrgStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        Optional<SysOrg> optionalSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()));
        if (!optionalSysOrg.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = optionalSysOrg.get();
        sysOrg.setStatus(requestBody.getStatus());

        sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-all-organization", method = RequestMethod.POST)
    public Object getAllOrg() {

        List<SysOrg> sysOrgArray = sysOrgRepository.findAll();

        sysOrgArray.forEach(sysOrg -> {
            if (sysOrg.getParent() != null) {
                sysOrg.setParent(sysOrg.getParent().toBuilder().parent(null).build());
            }
        });

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrgArray);
    }

}