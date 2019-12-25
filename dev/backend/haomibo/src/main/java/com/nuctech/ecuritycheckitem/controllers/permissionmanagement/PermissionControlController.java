/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PermissionControlController）
 * 文件名：	PermissionControlController.java
 * 描述：	Perminssion Control Controller.
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */
package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.PermissionService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.validation.annotations.ResourceId;
import com.nuctech.ecuritycheckitem.validation.annotations.RoleId;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.InputStream;
import java.util.List;

/**
 * Permission control controller.
 */
@RestController
@RequestMapping("/permission-management/permission-control")
public class PermissionControlController extends BaseController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    UserService userService;

    /**
     * Role create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleCreateRequestBody {

        @NotNull
        String roleNumber;
        @NotNull
        String roleName;
        @NotNull
        List<Long> resourceIdList;
        String note;

        SysRole convert2SysRole() { //create new object from input parameters
            return SysRole
                    .builder()
                    .roleNumber(this.getRoleNumber())
                    .roleName(this.getRoleName())
                    .status(SysOrg.Status.INACTIVE)
                    .note(this.note)
                    .build();
        }
    }

    /**
     * Role modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleModifyRequestBody {
        @NotNull
        @RoleId
        long roleId;

        @NotNull
        List<@ResourceId Long> resourceIdList;
    }


    /**
     * Role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            String roleName;
        }

        @NotNull
        @Min(1)
        int currentPage;
        @NotNull
        int perPage;
        Filter filter;
    }

    /**
     * Role  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class RoleGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.

        RoleGetByFilterAndPageRequestBody.Filter filter;
    }


    /**
     * Role delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleDeleteRequestBody {

        @NotNull
        long roleId;
    }

    /**
     * Data group datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            String dataGroupName;
        }

        @NotNull
        @Min(1)
        int currentPage;
        @NotNull
        int perPage;
        Filter filter;
    }

    /**
     * Data Group  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.

        DataGroupGetByFilterAndPageRequestBody.Filter filter;
    }


    /**
     * Data group create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupCreateRequestBody {

        @NotNull
        String dataGroupName;
        @NotNull
        String dataGroupNumber;
        @NotNull
        List<Long> userIdList;
        String note;

        SysDataGroup convert2SysDataGroup() { //create new object from input parameters

            return SysDataGroup
                    .builder()
                    .dataGroupNumber(this.getDataGroupNumber())
                    .dataGroupName(this.getDataGroupName())
                    .note(this.note)
                    .status(SysOrg.Status.INACTIVE)
                    .build();
        }
    }

    /**
     * Data group modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupModifyRequestBody {

        @NotNull
        long dataGroupId;
        @NotNull
        List<Long> userIdList;
    }

    /**
     * Data group delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupDeleteRequestBody {

        @NotNull
        long dataGroupId;
    }

    /**
     * DataGroup get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_USERS = "with_users";
        }

        @Pattern(regexp = GetAllType.BARE + "|" + GetAllType.WITH_USERS)
        String type = GetAllType.BARE;
    }

    /**
     * Role create request.
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_CREATE)
    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
    public Object roleCreate(
            @RequestBody @Valid RoleCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(permissionService.checkRoleNameExist(requestBody.getRoleName(), null)) { // if role name exists
            return new CommonResponseBody(ResponseMessage.USED_ROLE_NAME);
        }

        if(permissionService.checkRoleNumberExist(requestBody.getRoleNumber(), null)) { // if role number exists
            return new CommonResponseBody(ResponseMessage.USED_ROLE_NUMBER);
        }

        // Create role with created info.
        SysRole role = requestBody.convert2SysRole();
        List<Long> resourceIdList = requestBody.getResourceIdList();

        boolean result = permissionService.createRole(role, resourceIdList);
        if(result == false) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Role datatable request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/role/get-by-filter-and-page", method = RequestMethod.POST)
    public Object roleGetByFilterAndPage(
            @RequestBody @Valid RoleGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return null if validation fails
            return null;
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();
        String roleName = "";
        if(requestBody.getFilter() != null) {
            roleName = requestBody.getFilter().getRoleName();
        }

        PageResult<SysRole> result = permissionService.getRoleListByPage(roleName, currentPage, perPage);

        long total = result.getTotal();
        List<SysRole> data = result.getDataList();

        // Set filter.
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

        FilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

    /**
     * Role generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_EXPORT)
    @RequestMapping(value = "/role/xlsx", method = RequestMethod.POST)
    public Object roleGenerateExelFile(@RequestBody @Valid RoleGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String roleName = "";
        if(requestBody.getFilter() != null) {
            roleName = requestBody.getFilter().getRoleName();
        }

        List<SysRole> exportList = permissionService.getExportListByFilter(roleName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        InputStream inputStream = RoleExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=role.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Role generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/role/docx", method = RequestMethod.POST)
    public Object roleGenerateWordFile(@RequestBody @Valid RoleGenerateRequestBody requestBody,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String roleName = "";
        if(requestBody.getFilter() != null) {
            roleName = requestBody.getFilter().getRoleName();
        }

        List<SysRole> exportList = permissionService.getExportListByFilter(roleName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        InputStream inputStream = RoleWordView.buildWordDocument(exportList);//create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=role.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Role generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_PRINT)
    @RequestMapping(value = "/role/pdf", method = RequestMethod.POST)
    public Object roleGeneratePDFFile(@RequestBody @Valid RoleGenerateRequestBody requestBody,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all role list
        String roleName = "";
        if(requestBody.getFilter() != null) {
            roleName = requestBody.getFilter().getRoleName();
        }

        List<SysRole> exportList = permissionService.getExportListByFilter(roleName, requestBody.getIsAll(), requestBody.getIdList());
        RolePdfView.setResource(getFontResource()); //set font resource
        setDictionary();  //set dictionary data
        InputStream inputStream = RolePdfView.buildPDFDocument(exportList); //create inputstream of result to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=role.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Role modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_MODIFY)
    @RequestMapping(value = "/role/modify", method = RequestMethod.POST)
    public Object roleModify(
            @RequestBody @Valid RoleModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (permissionService.checkRoleExist(requestBody.getRoleId()) == false) {    // If role is not found, it's invalid parameter.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (permissionService.checkUserExist(requestBody.getRoleId())) { // If there are users assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }
        if (permissionService.checkUserGroupExist(requestBody.getRoleId())) { // If there are user groups assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }

        boolean result = permissionService.modifyRole(requestBody.getRoleId(), requestBody.getResourceIdList()); // Get role from database.
        if(result == false) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_RESOURCE, SimpleBeanPropertyFilter.filterOutAllExcept("resourceId", "parentResourceId", "resourceName", "resourceCaption")); //return all fields except specified params from SysResource model
        value.setFilters(filters);

        return value;
    }

    /**
     * Role delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_DELETE)
    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    public Object roleDelete(
            @RequestBody @Valid RoleDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!permissionService.checkRoleExist(requestBody.getRoleId())) { // Check if role is existing in the database.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (permissionService.checkUserExist(requestBody.getRoleId())) { // If there are users assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }
        if (permissionService.checkUserGroupExist(requestBody.getRoleId())) { // If there are user groups assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }
        permissionService.removeRole(requestBody.getRoleId());

        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_RESOURCE, SimpleBeanPropertyFilter.filterOutAllExcept("resourceId", "parentResourceId", "resourceName", "resourceCaption")); //return all fields except specified fields from SysResource model
        value.setFilters(filters);

        return value;
    }

    /**
     * Data group create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_CREATE)
    @RequestMapping(value = "/data-group/create", method = RequestMethod.POST)
    public Object dataGroupCreate(
            @RequestBody @Valid DataGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (permissionService.checkGroupNameExist(requestBody.getDataGroupName())) { //if group name exists
            return new CommonResponseBody(ResponseMessage.USED_DATA_GROUP_NAME);
        }
        if (permissionService.checkGroupNumberExist(requestBody.getDataGroupNumber())) { //if group number exists
            return new CommonResponseBody(ResponseMessage.USED_DATA_GROUP_NUMBER);
        }

        SysDataGroup dataGroup = requestBody
                .convert2SysDataGroup();

        List<Long> userIdList = requestBody.getUserIdList();
        permissionService.createDataGroup(dataGroup, userIdList);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Data group datatable request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/data-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object dataGroupGetByFilterAndPage(
            @RequestBody @Valid DataGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        String dataGroupName = "";
        if(requestBody.getFilter() != null) {
            dataGroupName = requestBody.getFilter().getDataGroupName();
        }

        PageResult<SysDataGroup> result = permissionService.getDataGroupListByPage(dataGroupName, currentPage, perPage);
        long total = result.getTotal();
        List<SysDataGroup> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
        value.setFilters(filters);

        return value;
    }

    /**
     * Data Group generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_EXPORT)
    @RequestMapping(value = "/data-group/xlsx", method = RequestMethod.POST)
    public Object dataGroupGenerateExcelFile(@RequestBody @Valid DataGroupGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String dataGroupName = "";
        if(requestBody.getFilter() != null) {
            dataGroupName = requestBody.getFilter().getDataGroupName();
        }

        List<SysDataGroup> exportList = permissionService.getExportGroupListByFilter(dataGroupName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        InputStream inputStream = DataGroupExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data-group.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Data Group generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/data-group/docx", method = RequestMethod.POST)
    public Object dataGroupGenerateWordFile(@RequestBody @Valid DataGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String dataGroupName = "";
        if(requestBody.getFilter() != null) {
            dataGroupName = requestBody.getFilter().getDataGroupName();
        }

        List<SysDataGroup> exportList = permissionService.getExportGroupListByFilter(dataGroupName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();//set dictionary data
        InputStream inputStream = DataGroupWordView.buildWordDocument(exportList);//create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data-group.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Data Group generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_PRINT)
    @RequestMapping(value = "/data-group/pdf", method = RequestMethod.POST)
    public Object dataGroupGeneratePDFFile(@RequestBody @Valid DataGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String dataGroupName = "";
        if(requestBody.getFilter() != null) {
            dataGroupName = requestBody.getFilter().getDataGroupName();
        }

        List<SysDataGroup> exportList = permissionService.getExportGroupListByFilter(dataGroupName, requestBody.getIsAll(), requestBody.getIdList());
        DataGroupPdfView.setResource(getFontResource()); //set font resource
        setDictionary();  //set dictionary data
        InputStream inputStream = DataGroupPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data-group.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Data group modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_MODIFY)
    @RequestMapping(value = "/data-group/modify", method = RequestMethod.POST)
    public Object dataGroupModify(
            @RequestBody @Valid DataGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!permissionService.checkDataGroupExist(requestBody.getDataGroupId())) { // If data group is not found, this request is invalid.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (permissionService.checkUserLookUpExist(requestBody.getDataGroupId())) { // If there are users assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }
        if (permissionService.checkDataGroupLookupExist(requestBody.getDataGroupId())) { // If there are user groups assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }

        List<Long> userIdList = requestBody.getUserIdList();
        permissionService.modifyDataGroup(requestBody.getDataGroupId(), userIdList);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Data group delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_DELETE)
    @RequestMapping(value = "/data-group/delete", method = RequestMethod.POST)
    public Object dataGroupDelete(
            @RequestBody @Valid DataGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!permissionService.checkDataGroupExist(requestBody.getDataGroupId())) { // If data group is not found, this request is invalid.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (permissionService.checkGroupChildExist(requestBody.getDataGroupId())) { // If data group has users, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }
        if (permissionService.checkUserLookUpExist(requestBody.getDataGroupId())) { // If there are users assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }
        if (permissionService.checkDataGroupLookupExist(requestBody.getDataGroupId())) { // If there are user groups assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }
        permissionService.removeDataGroup(requestBody.getDataGroupId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Data group get all request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/data-group/get-all", method = RequestMethod.POST)
    public Object dataGroupGetAll(@RequestBody @Valid DataGroupGetAllRequestBody requestBody,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDataGroup> sysDataGroupList = permissionService.findAllDataGroup(); // Get all first.

        // Filter. Type can be BARE or WITH_USERS.
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDataGroupList));
        String type = requestBody.getType();
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        switch (type) {
            case DataGroupGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users")); //return all fields except "users" from SysDataGroup model
                break;
            case DataGroupGetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields  from SysUser model
                break;
            default:
                break;
        }
        value.setFilters(filters);

        return value;
    }

    /**
     * Resource get all request.
     * @return
     */
    @RequestMapping(value = "/resource/get-all", method = RequestMethod.POST)
    public Object resourceGetAll() {

        List<SysResource> sysResourceList = permissionService.findAllResource();
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysResourceList));
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

}
