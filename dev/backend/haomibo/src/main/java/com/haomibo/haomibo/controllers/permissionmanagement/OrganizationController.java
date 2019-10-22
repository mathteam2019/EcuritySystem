package com.haomibo.haomibo.controllers.permissionmanagement;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.models.db.QSysOrg;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/permission-management/organization")
public class OrganizationController extends BaseController {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class CreateRequestBody {

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {

            return SysOrg
                    .builder()
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DeleteRequestBody {

        @NotNull
        Long orgId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class GetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String orgName;
            String status;
            String parentOrgName;
        }

        int currentPage;
        int perPage;

        Filter filter;


    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ModifyRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {
            return SysOrg
                    .builder()
                    .orgId(this.getOrgId())
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UpdateStatusRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
        String status;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder()
    public static class GetByFilterAndPageResponseBody {

        long total;
        int perPage;
        int currentPage;
        int lastPage;
        int from;
        int to;

        List<SysOrg> data;
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(
            @RequestBody @Valid CreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object modify(
            @RequestBody @Valid ModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(
            @RequestBody @Valid DeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org has children
        boolean isHavingChildren = sysOrgRepository.exists(QSysOrg.sysOrg.parentOrgId.eq(requestBody.getOrgId()));
        if (isHavingChildren) {
            return new CommonResponseBody(Constants.ResponseMessages.HAS_CHILDREN);
        }

        sysOrgRepository.delete(SysOrg.builder().orgId(requestBody.getOrgId()).build());

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public Object updateStatus(
            @RequestBody @Valid UpdateStatusRequestBody requestBody,
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
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public Object getAll() {

        List<SysOrg> sysOrgArray = sysOrgRepository.findAll();

        sysOrgArray.forEach(sysOrg -> {
            if (sysOrg.getParent() != null) {
                sysOrg.setParent(sysOrg.getParent().toBuilder().parent(null).build());
            }
        });


        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrgArray);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object getByFilterAndPage(@RequestBody @Valid GetByFilterAndPageRequestBody requestBody) {


        QSysOrg qSysOrg = QSysOrg.sysOrg;


        BooleanBuilder predicate = new BooleanBuilder(qSysOrg.isNotNull());

        GetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getOrgName())) {
                predicate.and(qSysOrg.orgName.contains(filter.getOrgName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(qSysOrg.status.contains(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getParentOrgName())) {
                predicate.and(qSysOrg.parent.orgName.contains(filter.getParentOrgName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // on server side, page is calculated from 0
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysOrgRepository.count(predicate);
        List<SysOrg> sysOrgList = sysOrgRepository.findAll(predicate, pageRequest).getContent();

        sysOrgList.forEach(sysOrg -> {
            if (sysOrg.getParent() != null) {
                sysOrg.setParent(sysOrg.getParent().toBuilder().parent(null).build());
            }
        });


        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                GetByFilterAndPageResponseBody
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + sysOrgList.size())
                        .data(sysOrgList)
                        .build());
    }

}
