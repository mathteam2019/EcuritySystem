/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（OrganizationManagementController）
 * 文件名：	OrganizationManagementController.java
 * 描述：	Organization management controller.
 * 作者名：	Sandy
 * 日期：	2019/10/19
 */

package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogPdfView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationPdfView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.OrganizationService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.InputStream;
import java.util.*;

/**
 * Organization management controller.
 */
@RestController
@RequestMapping("/permission-management/organization-management")
public class OrganizationManagementController extends BaseController {

    @Autowired
    OrganizationService organizationService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;



    /**
     * Organization create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationCreateRequestBody {

        @NotNull
        @Size(max = 50)
        String orgName;
        @NotNull
        @Size(max = 50)
        String orgNumber;
        @NotNull
        Long parentOrgId;
        @Size(max = 50)
        String leader;
        @Size(max = 50)
        String mobile;
        @Size(max = 500)
        String note;

        SysOrg convert2SysOrg() { //create new object from input parameters
            return SysOrg
                    .builder()
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Organization delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationDeleteRequestBody {

        @NotNull
        Long orgId;
    }

    /**
     * Organization datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String orgName;
            String status;
            String parentOrgName;
        }

        @NotNull
        @Min(1)
        int currentPage;
        @NotNull
        int perPage;
        String sort;
        Filter filter;
    }

    /**
     * Organization  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        OrganizationGetByFilterAndPageRequestBody.Filter filter;
        String locale;
    }

    /**
     * Organization modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationModifyRequestBody {

        @NotNull
        Long orgId;
        @NotNull
        @Size(max = 50)
        String orgName;
        @NotNull
        @Size(max = 50)
        String orgNumber;
        @NotNull
        Long parentOrgId;
        @Size(max = 50)
        String leader;
        @Size(max = 50)
        String mobile;
        @Size(max = 500)
        String note;

        SysOrg convert2SysOrg() { //create new object from input parameters
            return SysOrg
                    .builder()
                    .orgId(this.getOrgId())
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Organization update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationUpdateStatusRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
        String status;
    }


    /**
     * Organization get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_PARENT = "with_parent";
            static final String WITH_CHILDREN = "with_children";
            static final String WITH_USERS = "with_users";
            static final String WITH_PARENT_AND_USERS = "with_parent_and_users";
            static final String WITH_CHILDREN_AND_USERS = "with_children_and_users";
            static final String WITH_GRAPHIC = "with_graphic";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_PARENT + "|" +
                GetAllType.WITH_CHILDREN + "|" +
                GetAllType.WITH_USERS + "|" +
                GetAllType.WITH_PARENT_AND_USERS + "|" +
                GetAllType.WITH_CHILDREN_AND_USERS + "|" +
                GetAllType.WITH_GRAPHIC)
        String type = GetAllType.BARE;

    }

    /**
     * Organization create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ORG_CREATE)
    @RequestMapping(value = "/organization/create", method = RequestMethod.POST)
    public Object organizationCreate(
            @RequestBody @Valid OrganizationCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(organizationService.checkOrgNameExist(requestBody.getOrgName(), null)) { //check OrgName Exist
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("UsedOrgName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ORG_NAME);
        }
        if(organizationService.checkOrgNumberExist(requestBody.getOrgNumber(), null)) { //check org number exist
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("UsedOrgNumber", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ORG_NUMBER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();
        if (organizationService.createOrganization(requestBody.getParentOrgId(), sysOrg)) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * check org exists
     * @param orgId
     * @return
     */
    private Object checkExist(Long orgId) {

//        if(organizationService.checkDataGroupExist(orgId)) {
//            return new CommonResponseBody(ResponseMessage.HAS_DATA_GROUPS);
//        }
//
//        if(organizationService.checkUserGroupExist(orgId)) {
//            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
//        }

        if(organizationService.checkUserExist(orgId)) {
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }

//        if(organizationService.checkRoleExist(orgId)) {
//            return new CommonResponseBody(ResponseMessage.HAS_ROLES);
//        }
//
//        if(organizationService.checkFieldExist(orgId)) {
//            return new CommonResponseBody(ResponseMessage.HAS_FIELDS);
//        }
        return null;
    }

    /**
     * Organization modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ORG_MODIFY)
    @RequestMapping(value = "/organization/modify", method = RequestMethod.POST)
    public Object organizationModify(
            @RequestBody @Valid OrganizationModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(organizationService.checkOrgNameExist(requestBody.getOrgName(), requestBody.getOrgId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("UsedOrgName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ORG_NAME);
        }
        if(organizationService.checkOrgNumberExist(requestBody.getOrgNumber(), requestBody.getOrgId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("UsedOrgNumber", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_ORG_NUMBER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        if (organizationService.modifyOrganization(requestBody.getOrgId(), requestBody.getParentOrgId(), sysOrg)) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * Organization delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ORG_DELETE)
    @RequestMapping(value = "/organization/delete", method = RequestMethod.POST)
    public Object organizationDelete(
            @RequestBody @Valid OrganizationDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (organizationService.deleteOrganization(requestBody.getOrgId())) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("Organization.Error.ActiveOrg", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.ACTIVE_ORG);
        }
    }

    /**
     * Organization update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ORG_UPDATE_STATUS)
    @RequestMapping(value = "/organization/update-status", method = RequestMethod.POST)
    public Object organizationUpdateStatus(
            @RequestBody @Valid OrganizationUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(organizationService.checkChildrenExist(requestBody.getOrgId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        Object checkResult = checkExist(requestBody.getOrgId());
        if(checkResult != null) {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("HaveUser", null, currentLocale), "", null, false, "", "");

            return checkResult;
        }

        if (organizationService.updateOrganizationStatus(requestBody.getOrgId(), requestBody.getStatus())) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Org", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * Organization get all request.
     * BARE, WITH_PARENT, WITH_CHILDREN, WITH_USERS, WITH_PARENT_AND_USERS, WITH_CHILDREN_AND_USERS.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/organization/get-all", method = RequestMethod.POST)
    public Object organizationGetAll(@RequestBody @Valid OrganizationGetAllRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        boolean isAll = false;
        if(requestBody.getType().equals(OrganizationGetAllRequestBody.GetAllType.WITH_GRAPHIC)) {
            //isAll = true;
        }

        List<SysOrg> sysOrgList = organizationService.getAllOrganization(isAll);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysOrgList));
        String type = requestBody.getType();
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        switch (type) { // Set filters for different type.
            case OrganizationGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users", "children")); //return all fields except specified fields from SysOrg model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children")); //return all fields except specified fields from SysOrg model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_GRAPHIC:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children")); //return all fields except specified fields from SysOrg model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users")); //return all fields except specified fields from SysOrg model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "children")) //return all fields except specified fields from SysOrg model
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children")) //return all fields except specified fields from SysOrg model
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent")) //return all fields except specified fields from SysOrg model
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
                break;
            default:
                break;
        }
        value.setFilters(filters);

        return value;
    }

    /**
     * Organization datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/organization/get-by-filter-and-page", method = RequestMethod.POST)
    public Object organizationGetByFilterAndPage(
            @RequestBody @Valid OrganizationGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage--;

        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }

        PageResult<SysOrg> result = organizationService.getOrganizationByFilterAndPage(sortBy, order,
                requestBody.getFilter().getOrgName(), //get org name from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getParentOrgName(), //get parent org name from input parameter
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysOrg> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set end index of current page
                        .data(data) //set data
                        .build()));

        // Set filters.
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children", "users")); // return all fields except "children" and "users" from SysOrg model
        value.setFilters(filters);

        return value;
    }

    /**
     * get export list
     * @param orgList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysOrg> getExportList(List<SysOrg> orgList, boolean isAll, String idList) {
        List<SysOrg> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < orgList.size(); i++) {
                SysOrg org = orgList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(org.getOrgId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(org);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < orgList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(orgList.get(i));
            }
        }
        return exportList;
    }

    /**
     * Organization generate excel request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ORG_EXPORT)
    @RequestMapping(value = "/organization/xlsx", method = RequestMethod.POST)
    public Object organizationGenerateExcelFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(sortBy, order,
                requestBody.getFilter().getOrgName(), //get org name from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getParentOrgName() //get parent org name from input parameter
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data
        OrganizationExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            OrganizationExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            OrganizationExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = OrganizationExcelView.buildExcelDocument(exportList);//create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Organization generate word request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/organization/docx", method = RequestMethod.POST)
    public Object organizationGenerateWordFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(sortBy, order,
                requestBody.getFilter().getOrgName(), //get org name from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getParentOrgName() //get parent org name from input parameter
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale());//set dictionary data
        OrganizationWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            OrganizationWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            OrganizationWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = OrganizationWordView.buildWordDocument(exportList);//create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Organization generate pdf request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_PRINT)
    @RequestMapping(value = "/organization/pdf", method = RequestMethod.POST)
    public Object organizationGeneratePdfFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(sortBy, order,
                requestBody.getFilter().getOrgName(), //get org name from input parameter
                requestBody.getFilter().getStatus(), //get status from input parameter
                requestBody.getFilter().getParentOrgName() //get parent org name from input parameter
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());

        OrganizationPdfView.setResource(getFontResource()); //set font resource
        setDictionary(requestBody.getLocale()); //set dictionary data
        OrganizationPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            OrganizationPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            OrganizationPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = OrganizationPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

}
