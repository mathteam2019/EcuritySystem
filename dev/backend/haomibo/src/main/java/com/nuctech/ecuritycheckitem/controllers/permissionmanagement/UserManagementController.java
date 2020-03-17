/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserManagementController）
 * 文件名：	UserManagementController.java
 * 描述：	User management controller.
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */

package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.DataGroupExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import com.nuctech.ecuritycheckitem.utils.CryptUtil;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/permission-management/user-management")
public class UserManagementController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    PlatformOtherService platformOtherService;



    /**
     * User create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserCreateRequestBody {

        static class PasswordType {
            static final String DEFAULT = "default";
            static final String OTHER = "other";
        }

        @NotNull
        long orgId;
        @NotNull
        @Size(max = 50)
        String userName;
        @NotNull
        @Size(max = 20)
        String userAccount;
        @NotNull
        @Pattern(regexp = PasswordType.DEFAULT + "|" + PasswordType.OTHER)
        String passwordType;
        String passwordValue;
        @NotNull
        @Size(max = 50)
        String userNumber;
        @NotNull
        @Pattern(regexp = SysUser.Gender.MALE + "|" + SysUser.Gender.FEMALE + "|" + SysUser.Gender.OTHER)
        String gender;
        @NotNull
        @Size(max = 50)
        String identityCard;
        @Size(max = 50)
        String post;
        @Size(max = 20)
        String education;
        @Size(max = 20)
        String degree;
        @Email
        @Size(max = 50)
        String email;
        @Size(max = 20)
        String mobile;
        @Size(max = 50)
        String address;
        @Size(max = 500)
        String note;

        private MultipartFile portrait;

        SysUser convert2SysUser() { //create new object from input parameters

            return SysUser.builder()
                    .orgId(this.getOrgId())
                    .userName(this.getUserName())
                    .userAccount(this.getUserAccount())
                    .password(PasswordType.DEFAULT.equals(this.getPasswordType()) ? Constants.DEFAULT_PASSWORD_FOR_NEW_SYS_USER : this.getPasswordValue())
                    .userNumber(this.getUserNumber())
                    .gender(this.getGender())
                    .identityCard(this.getIdentityCard())
                    .post(this.getPost())
                    .education(this.getEducation())
                    .degree(this.getDegree())
                    .email(this.getEmail())
                    .mobile(this.getMobile())
                    .address(this.getAddress())
                    .category(SysUser.Category.NORMAL)
                    .status(SysUser.Status.INACTIVE)
                    .note(this.getNote())
                    .dataRangeCategory(SysUser.DataRangeCategory.PERSON.getValue())
                    .build();
        }
    }

    /**
     * User modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserModifyRequestBody {

        @NotNull
        long userId;
        @NotNull
        long orgId;
        @NotNull
        String userName;
        String userAccount;
        @NotNull
        String userNumber;
        @NotNull
        @Pattern(regexp = SysUser.Gender.MALE + "|" + SysUser.Gender.FEMALE + "|" + SysUser.Gender.OTHER)
        String gender;
        @NotNull
        String identityCard;
        String post;
        String education;
        String degree;
        @Email
        String email;
        String mobile;
        String address;
        String note;
        @NotNull
        @Pattern(regexp = UserCreateRequestBody.PasswordType.DEFAULT + "|" + UserCreateRequestBody.PasswordType.OTHER)
        String passwordType;
        String passwordValue;

        private MultipartFile portrait;

        SysUser convert2SysUser() { //create new object from input parameters

            return SysUser.builder()
                    .userId(this.getUserId())
                    .orgId(this.getOrgId())
                    .userName(this.getUserName())
                    .userAccount(this.getUserAccount())
                    .userNumber(this.getUserNumber())
                    .gender(this.getGender())
                    .password(UserCreateRequestBody.PasswordType.DEFAULT.equals(this.getPasswordType()) ? Constants.DEFAULT_PASSWORD_FOR_NEW_SYS_USER : this.getPasswordValue())
                    .identityCard(this.getIdentityCard())
                    .post(this.getPost())
                    .education(this.getEducation())
                    .degree(this.getDegree())
                    .email(this.getEmail())
                    .mobile(this.getMobile())
                    .address(this.getAddress())
                    .category(SysUser.Category.NORMAL)
                    .note(this.getNote())
                    .build();
        }
    }

    /**
     * User get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_ORG_TREE = "with_org_tree";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_ORG_TREE)
        String type = GetAllType.BARE;
    }

    /**
     * User datatable request body.
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
            @Pattern(regexp = SysUser.Status.ACTIVE + "|" +
                    SysUser.Status.INACTIVE + "|" +
                    SysUser.Status.PENDING + "|" +
                    SysUser.Status.BLOCKED)
            String status;
            @Pattern(regexp = SysUser.Gender.MALE + "|" +
                    SysUser.Gender.FEMALE + "|" +
                    SysUser.Gender.OTHER)
            String gender;
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
        String sort;
        UserGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * User update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserUpdateStatusRequestBody {

        @NotNull
        Long userId;

        @NotNull
        @Pattern(regexp = SysUser.Status.ACTIVE + "|" + SysUser.Status.INACTIVE + "|" + SysUser.Status.BLOCKED + "|" + SysUser.Status.PENDING)
        String status;
    }

    /**
     * User update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserModifyPasswordRequestBody {

        @NotNull
        Long userId;
        String password;
    }


    /**
     * User group create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupCreateRequestBody {

        @NotNull
        String groupNumber;
        @NotNull
        String groupName;
        @NotNull
        List<Long> userIdList;
        String note;
    }

    /**
     * User group modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupModifyRequestBody {

        @NotNull
        long userGroupId;
        @NotNull
        List<Long> userIdList;
    }

    /**
     * User group datatable request body.
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
        String sort;
        UserGroupGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * User group delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupDeleteRequestBody {
        @NotNull
        long userGroupId;
    }

    /**
     * User create request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_CREATE)
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Object userCreate(
            @ModelAttribute @Valid UserCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {//return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkOrgExist(requestBody.getOrgId())) {// If organization is not found, this is invalid request.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (userService.checkAccountExist(requestBody.getUserAccount(), null)) { // Check if user account is duplicated.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedAccount", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }

        if (userService.checkNumberExist(requestBody.getUserNumber(), null)) { // Check if user account is duplicated.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("User.Number", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_USER_NUMBER);
        }

        if (UserCreateRequestBody.PasswordType.OTHER.equals(requestBody.getPasswordType()) && (requestBody.getPasswordValue() == null || requestBody.getPasswordValue().length() < 6)) { // Check password.

            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (StringUtils.isNotBlank(requestBody.getEmail()) && userService.checkEmailExist(requestBody.getEmail(), null)) { // Check if user email is duplicated.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedEmail", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_EMAIL);
        }
        if (StringUtils.isNotBlank(requestBody.getMobile()) && userService.checkMobileExist(requestBody.getMobile(), null)) { // Check if user phone number is duplicated.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedMobile", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_MOBILE);
        }
        SysUser sysUser = requestBody.convert2SysUser();
//        if(UserCreateRequestBody.PasswordType.DEFAULT.equals(requestBody.getPasswordType())) {
//            sysUser.setPassword(platformOtherService.findAll().get(0).getInitialPassword());
//        }


        sysUser.setPassword(CryptUtil.encode(sysUser.getPassword()));
        userService.createUser(sysUser, requestBody.getPortrait());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User modify request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_MODIFY)
    @RequestMapping(value = "/user/modify", method = RequestMethod.POST)
    public Object userModify(
            @ModelAttribute @Valid UserModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkUserExist(requestBody.getUserId())) { // Check if user is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (!userService.checkOrgExist(requestBody.getOrgId())) { // Check if org is valid.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (userService.checkAccountExist(requestBody.getUserAccount(), requestBody.getUserId())) { // Check if user account is duplicated.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedAccount", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }
        if (StringUtils.isNotBlank(requestBody.getMobile()) && userService.checkMobileExist(requestBody.getMobile(), requestBody.getUserId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedMobile", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_MOBILE);
        }
        if (StringUtils.isNotBlank(requestBody.getEmail()) && userService.checkEmailExist(requestBody.getEmail(), requestBody.getUserId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("UsedEmail", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_EMAIL);
        }
        SysUser sysUser = requestBody.convert2SysUser();
//        if(UserCreateRequestBody.PasswordType.DEFAULT.equals(requestBody.getPasswordType())) {
//            sysUser.setPassword(platformOtherService.findAll().get(0).getInitialPassword());
//        }
        sysUser.setPassword(CryptUtil.encode(sysUser.getPassword()));
        userService.modifyUser(sysUser, requestBody.getPortrait());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User datatable request.
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

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if (requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName(); //get user name from input parameters
            status = requestBody.getFilter().getStatus(); //get status from input parameters
            gender = requestBody.getFilter().getGender(); //get gender from input parameters
            orgId = requestBody.getFilter().getOrgId(); //get org id  from input parameters
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

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SysUser> result = userService.getUserListByPage(sortBy, order, userName, status, gender, orgId, currentPage, perPage);
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

        FilterProvider filters = ModelJsonFilters.getDefaultFilters().addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children", "users")) //return all fields except "children", "users" from SysOrg model
                .addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users")); //return all fields except users from SysDataGroup model
        value.setFilters(filters);

        return value;
    }

    /**
     * User generate excel request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_EXPORT)
    @RequestMapping(value = "/user/xlsx", method = RequestMethod.POST)
    public Object userGenerateExcelFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if (requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName(); //get user name from input parameters
            status = requestBody.getFilter().getStatus(); //get status from input parameters
            gender = requestBody.getFilter().getGender(); //get gender from input parameters
            orgId = requestBody.getFilter().getOrgId(); //get org id  from input parameters
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
        List<SysUser> exportList = userService.getExportUserListByPage(sortBy, order, userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        InputStream inputStream = UserExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User generate word request.
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

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if (requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName(); //get user name from input parameters
            status = requestBody.getFilter().getStatus(); //get status from input parameters
            gender = requestBody.getFilter().getGender(); //get gender from input parameters
            orgId = requestBody.getFilter().getOrgId(); //get org id  from input parameters
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
        List<SysUser> exportList = userService.getExportUserListByPage(sortBy, order, userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();//set dictionary data
        UserWordView.setMessageSource(messageSource);
        InputStream inputStream = UserWordView.buildWordDocument(exportList);//create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User generate pdf request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_PRINT)
    @RequestMapping(value = "/user/pdf", method = RequestMethod.POST)
    public Object userGeneratePdfFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if (requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName(); //get user name from input parameters
            status = requestBody.getFilter().getStatus(); //get status from input parameters
            gender = requestBody.getFilter().getGender(); //get gender from input parameters
            orgId = requestBody.getFilter().getOrgId(); //get org id  from input parameters
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
        List<SysUser> exportList = userService.getExportUserListByPage(sortBy, order, userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        UserPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        UserPdfView.setMessageSource(messageSource);
        InputStream inputStream = UserPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User update status request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_UPDATE_STATUS)
    @RequestMapping(value = "/user/update-status", method = RequestMethod.POST)
    public Object userUpdateStatus(
            @RequestBody @Valid UserUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkUserExist(requestBody.getUserId())) { // Check if org is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(requestBody.getStatus().equals(SysUser.Status.INACTIVE)) {

            if (userService.checkParentUserGroupExist(requestBody.getUserId())) { // Check if org is existing.
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("User", null, currentLocale),
                        messageSource.getMessage("HaveUserGroup", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
            }
            if (userService.checkParentDataGroupExist(requestBody.getUserId())) { // Check if org is existing.
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("User", null, currentLocale),
                        messageSource.getMessage("HaveDataGroup", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_DATA_GROUPS);
            }
            if (userService.checkRoleExist(requestBody.getUserId())) { // Check if org is existing.
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("User", null, currentLocale),
                        messageSource.getMessage("HaveRole", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_ROLES);
            }
        }
        userService.updateStatus(requestBody.getUserId(), requestBody.getStatus());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User update status request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_UPDATE_STATUS)
    @RequestMapping(value = "/user/modify-password", method = RequestMethod.POST)
    public Object userModifyPassword(
            @RequestBody @Valid UserModifyPasswordRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkUserExist(requestBody.getUserId())) { // Check if org is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(!userService.modifyPassword(requestBody.getUserId(), requestBody.getPassword())) {
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("User.Error.NoLocking", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USER_NOT_LOCK);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User get all request.
     * BARE, WITH_ORG_TREE.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/user/get-all", method = RequestMethod.POST)
    public Object userGetAll(@RequestBody @Valid UserGetAllRequestBody requestBody,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysUser> sysUserList = userService.findAllUser();
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysUserList));
        String type = requestBody.getType();
        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        switch (type) {
            case UserGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups")); //return all fields except specified fields from SysUser model
                break;
            case UserGetAllRequestBody.GetAllType.WITH_ORG_TREE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("roles", "dataGroups")) //return all fields except specified fields from SysUser model
                        .addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children")); //return all fields except specified fields from SysOrg model
                break;
            default:
                break;
        }

        value.setFilters(filters);
        return value;
    }

    /**
     * User group create request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_CREATE)
    @RequestMapping(value = "/user-group/create", method = RequestMethod.POST)
    public Object userGroupCreate(
            @RequestBody @Valid UserGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (userService.checkGroupNameExist(requestBody.getGroupName(), null)) { //check group name existance
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("UsedGroupName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_USER_GROUP_NAME);
        }
        if (userService.checkGroupNumberExist(requestBody.getGroupNumber(), null)) { //check group number existance
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("UsedGroupNumber", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_USER_GROUP_NUMBER);
        }

        // Create user group with created info.
        SysUserGroup userGroup = (SysUserGroup) SysUserGroup
                .builder()
                .groupName(requestBody.getGroupName())
                .groupNumber(requestBody.getGroupNumber())
                .dataRangeCategory(SysUserGroup.DataRangeCategory.PERSON.getValue())
                .note(requestBody.getNote())
                .build()
                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        List<Long> userIdList = requestBody.getUserIdList();
        userService.createUserGroup(userGroup, userIdList);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User group datatable request.
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
        // Pagination.
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        String groupName = "";
        String userName = "";
        if (requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
            userName = requestBody.getFilter().getUserName();
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
        PageResult<SysUserGroup> result = userService.getUserGroupListByPage(sortBy, order, groupName, userName, currentPage, perPage);
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
                .addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users")) //return all fields except "users" from SysDataGroup model
                .addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources")); //return all fields except "resources" from SysRole model
        value.setFilters(filters);

        return value;
    }

    /**
     * User Group generate excel request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_EXPORT)
    @RequestMapping(value = "/user-group/xlsx", method = RequestMethod.POST)
    public Object userGroupGenerateExcelFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        String userName = "";
        if (requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
            userName = requestBody.getFilter().getUserName();
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

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(sortBy, order, groupName, userName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        UserGroupExcelView.setMessageSource(messageSource);
        InputStream inputStream = UserGroupExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Group generate excel request.
     */

    @RequestMapping(value = "/user-group/docx", method = RequestMethod.POST)
    public Object userGroupGenerateWordFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        String userName = "";
        if (requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
            userName = requestBody.getFilter().getUserName();
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

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(sortBy, order, groupName, userName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data
        UserGroupWordView.setMessageSource(messageSource);
        InputStream inputStream = UserGroupWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User Group generate pdf request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_PRINT)
    @RequestMapping(value = "/user-group/pdf", method = RequestMethod.POST)
    public Object userGroupGeneratePDFFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        String userName = "";
        if (requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
            userName = requestBody.getFilter().getUserName();
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

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(sortBy, order, groupName, userName, requestBody.getIsAll(), requestBody.getIdList());
        UserGroupWordView.setMessageSource(messageSource);
        setDictionary();  //set dictionary data
        InputStream inputStream = UserGroupPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User group modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_MODIFY)
    @RequestMapping(value = "/user-group/modify", method = RequestMethod.POST)
    public Object userGroupModify(@RequestBody @Valid UserGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkUserGroupExist(requestBody.getUserGroupId())) { // If user group is not found, this is invalid request.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        userService.modifyUserGroup(requestBody.getUserGroupId(), requestBody.getUserIdList());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * User group delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_DELETE)
    @RequestMapping(value = "/user-group/delete", method = RequestMethod.POST)
    public Object userGroupDelete(
            @RequestBody @Valid UserGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!userService.checkUserGroupExist(requestBody.getUserGroupId())) { // If user group is not found, this is invalid request.
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
//        if (userService.checkUserGroupUserExist(requestBody.getUserGroupId())) { // If user group has users, it can't be delete.
//            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
//                    "", messageSource.getMessage("UserGroup", null, currentLocale),
//                    messageSource.getMessage("HaveUser", null, currentLocale), "", null, false, "", "");
//
//            return new CommonResponseBody(ResponseMessage.HAS_USERS);
//        }
        if (userService.checkUserGroupRoleExist(requestBody.getUserGroupId())) {
            /*
             * Todo
             *  return HAS_ROLE
             * */
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("UserGroup", null, currentLocale),
                    messageSource.getMessage("HaveRole", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_ROLES);
        }
        userService.removeUserGroup(requestBody.getUserGroupId()); // Delete.
        return new CommonResponseBody(ResponseMessage.OK);
    }
}
