/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignPermissionManagementController）
 * 文件名：	AssignPermissionManagementController.java
 * 描述：	Assign permission Management Controller.
 * 作者名：	Sandy
 * 日期：	2019/10/22
 */

package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.logmanagement.AuditLogPdfView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.AssignPermissionService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.validation.annotations.RoleId;
import com.nuctech.ecuritycheckitem.validation.annotations.UserDataRangeCategory;
import com.nuctech.ecuritycheckitem.validation.annotations.UserId;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Assign permission management controller.
 */
@RestController
@RequestMapping("/permission-management/assign-permission-management")
public class AssignPermissionManagementController extends BaseController {

    @Autowired
    AssignPermissionService assignPermissionService;

    @Autowired
    UserService userService;

    /**
     * Request body for assigning role and data range to user.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserAssignRoleAndDataRangeRequestBody {

        @NotNull
        @UserId
        Long userId;
        @NotNull
        List<@RoleId Long> roleIdList;
        @NotNull
        @UserDataRangeCategory
        String dataRangeCategory;
        long selectedDataGroupId;

    }

    /**
     * Request body for assigning role and data range to user group.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupAssignRoleAndDataRangeRequestBody {

        @NotNull
        Long userGroupId;
        @NotNull
        List<@RoleId Long> roleIdList;
        @NotNull
        @UserDataRangeCategory
        String dataRangeCategory;
        long selectedDataGroupId;
    }

    /**
     * User with role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String userName;
            Long orgId;
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
     * User  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        UserGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * User group with role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String groupName;
            String userName;
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
     * User Group generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        UserGroupGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * get export list
     *
     * @param userList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysUser> getExportList(List<SysUser> userList, boolean isAll, String idList) {
        List<SysUser> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < userList.size(); i++) {
                SysUser user = userList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(user.getUserId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(user);
                }
            }
        } else {
            exportList = userList;
        }
        return exportList;
    }

    /**
     * User assign role and data range request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_CREATE)
    @RequestMapping(value = "/user/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userAssignRoleAndDataRange(
            @RequestBody @Valid UserAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (assignPermissionService.userAssignRoleAndDataRange(requestBody.getUserId(), requestBody.getRoleIdList(), requestBody.getDataRangeCategory(), requestBody.getSelectedDataGroupId())) {

            SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
            List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

            MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            filters.addFilter(ModelJsonFilters.FILTER_SYS_RESOURCE, SimpleBeanPropertyFilter.filterOutAllExcept("resourceId", "parentResourceId", "resourceName", "resourceCaption")); //return all fields except specified fields from SysResource model
            value.setFilters(filters);

            return value;
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * User group assign role and data range request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_CREATE)
    @RequestMapping(value = "/user-group/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userGroupAssignRoleAndDataRange(
            @RequestBody @Valid UserGroupAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (assignPermissionService.userGroupAssignRoleAndDataRange(requestBody.getUserGroupId(), requestBody.getRoleIdList(), requestBody.getDataRangeCategory(), requestBody.getSelectedDataGroupId())) {

            SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
            List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

            MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            filters.addFilter(ModelJsonFilters.FILTER_SYS_RESOURCE, SimpleBeanPropertyFilter.filterOutAllExcept("resourceId", "parentResourceId", "resourceName", "resourceCaption")); //return all fields except specified fields from SysResource model

            value.setFilters(filters);

            return value;
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

    }

    /**
     * Role get all request.
     *
     * @return
     */
    @RequestMapping(value = "/role/get-all", method = RequestMethod.POST)
    public Object roleGetAll() {

        List<SysRole> sysRoleList = assignPermissionService.roleGetAll(); // Get all role list from DB.

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysRoleList)); // Set filter for response json.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources"));
        value.setFilters(filters);

        return value;
    }

    /**
     * Role get all request.
     *
     * @return
     */
    @RequestMapping(value = "/user-group/get-all", method = RequestMethod.POST)
    public Object userGroupGetAll() {

        List<SysUserGroup> sysUserGroupList = assignPermissionService.userGroupGetAll(); // Get all sys user group list from DB.

        // Set filter for response json.
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysUserGroupList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"))  //return all fields except specified fields from SysUser model
                .addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources")); //return all fields except specified fields from SysRole model
        value.setFilters(filters);

        return value;
    }

    /**
     * User with role datatable request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/user/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGetByFilterAndPage(
            @RequestBody @Valid UserGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage--;
        PageResult<SysUser> result = assignPermissionService.userGetByFilterAndPage(
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getOrgId(), //get ord id  from input parameter
                requestBody.getFilter().getRoleName(), //get role name from input parameter
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysUser> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "children", "users")) //return all fields except specified fields from SysOrg model
                .addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources")) //return all fields except resources from SysRole model
                .addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users")); //return all fields except users from SysDataGroup model
        value.setFilters(filters);

        return value;
    }

    /**
     * User generate file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_EXPORT)
    @RequestMapping(value = "/user/xlsx", method = RequestMethod.POST)
    public Object userGenerateExcelFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user list from service
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList()); //get export list
        setDictionary(); //set dictionary data
        AssignUserExcelView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User generate file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/user/docx", method = RequestMethod.POST)
    public Object userGenerateWordFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user list
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        AssignUserWordView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User generate file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_PRINT)
    @RequestMapping(value = "/user/pdf", method = RequestMethod.POST)
    public Object userGeneratePDFFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user list
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList());
        AssignUserPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        AssignUserPdfView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User group with role datatable request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/user-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGroupGetByFilterAndPage(
            @RequestBody @Valid UserGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage--;
        PageResult<SysUserGroup> result = assignPermissionService.userGroupGetByFilterAndPage(
                requestBody.getFilter().getGroupName(), //get group name from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getRoleName(), //get role name from input parameter
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysUserGroup> data = result.getDataList();

        // Set filters.
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

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")) //return all fields except specified fields from SysUser model
                .addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources")) //return all fields except resources from SysRole model
                .addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users")); //return all fields except users from SysDataGroup model
        value.setFilters(filters);

        return value;
    }

    /**
     * get user-group export list
     * @param userGroupList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysUserGroup> getUserGroupExportList(List<SysUserGroup> userGroupList, boolean isAll, String idList) {
        List<SysUserGroup> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < userGroupList.size(); i++) {
                SysUserGroup userGroup = userGroupList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(userGroup.getUserGroupId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(userGroup);
                }
            }
        } else {
            exportList = userGroupList;
        }
        return exportList;
    }

    /**
     * User Group generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_EXPORT)
    @RequestMapping(value = "/user-group/xlsx", method = RequestMethod.POST)
    public Object userGroupGenerateExcelFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(), //get group name from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getRoleName() //get role name from input parameter
        );

        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList()); //get export list
        setDictionary(); //set dictionary data
        AssignUserGroupExcelView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserGroupExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Group generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */

    @RequestMapping(value = "/user-group/docx", method = RequestMethod.POST)
    public Object userGroupGenerateWordFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(), //get group name from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getRoleName() //get role name from input parameter
        );

        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        AssignUserGroupWordView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserGroupWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * User Group generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_PRINT)
    @RequestMapping(value = "/user-group/pdf", method = RequestMethod.POST)
    public Object userGroupGeneratePDFFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(), //get group name from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getRoleName() //get role name from input parameter
        );

        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList());
        AssignUserGroupPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        AssignUserGroupPdfView.setMessageSource(messageSource);
        InputStream inputStream = AssignUserGroupPdfView.buildPDFDocument(exportList);//create inputstream of result to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
