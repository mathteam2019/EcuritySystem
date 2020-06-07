/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FieldManagementController）
 * 文件名：	FieldManagementController.java
 * 描述：	Field management controller.
 * 作者名：	Sandy
 * 日期：	2019/10/30
 */

package com.nuctech.ecuritycheckitem.controllers.fieldmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceFieldWordView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementExcelView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementPdfView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.fieldmanagement.FieldService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
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
 * Field management controller.
 */
@RestController
@RequestMapping("/site-management")
public class FieldManagementController extends BaseController {

    @Autowired
    FieldService fieldService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;


    /**
     * Field create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldCreateRequestBody {

        @NotNull
        @Size(max = 50)
        String fieldSerial;
        @NotNull
        @Size(max = 50)
        String fieldDesignation;
        @NotNull
        Long parentFieldId;
        @Size(max = 50)
        String leader;
        @Size(max = 50)
        String mobile;
        @Size(max = 255)
        String note;
        SysField convert2SysField() { //create new object from input parameters
            return SysField
                    .builder()
                    .fieldSerial(this.getFieldSerial())
                    .fieldDesignation(this.getFieldDesignation())
                    .parentFieldId(this.getParentFieldId())
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysField.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Field delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldDeleteRequestBody {

        @NotNull
        Long fieldId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldGetAllRequestBody {
        String isAll = "0";
    }

    /**
     * Field datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            Long parentFieldId;
            String status;
            String fieldDesignation;
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
     * Field  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;
        String sort;
        FieldGetByFilterAndPageRequestBody.Filter filter;
        String locale;
    }

    /**
     * Field modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldModifyRequestBody {

        @NotNull
        Long fieldId;
        @NotNull
        String fieldSerial;
        @NotNull
        String fieldDesignation;
        @NotNull
        Long parentFieldId;
        String leader;
        String mobile;
        String note;

        SysField convert2SysField() { //create new object from input parameters
            return SysField
                    .builder()
                    .fieldId(this.getFieldId())
                    .fieldSerial(this.getFieldSerial())
                    .fieldDesignation(this.getFieldDesignation())
                    //.orgId(this.getOrgId())
                    .parentFieldId(this.getParentFieldId())
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Field update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldUpdateStatusRequestBody {

        @NotNull
        Long fieldId;

        @NotNull
        @Pattern(regexp = SysField.Status.ACTIVE + "|" + SysField.Status.INACTIVE)
        String status;
    }

    /**
     * Field create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_CREATE)
    @RequestMapping(value = "/field/create", method = RequestMethod.POST)
    public Object fieldCreate(
            @RequestBody @Valid FieldCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (requestBody.getParentFieldId() != 0 && !fieldService.checkFieldExist(requestBody.getParentFieldId())) {// Check if parent field is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(fieldService.checkFieldSerial(requestBody.getFieldSerial(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("UsedFieldSerial", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_FIELD_SERIAL);
        }
        if(fieldService.checkFieldDesignation(requestBody.getFieldDesignation(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("UsedFieldDesignation", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_FIELD_DESIGNATION);
        }

        SysField sysField = requestBody.convert2SysField();
        fieldService.createField(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Field modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_MODIFY)
    @RequestMapping(value = "/field/modify", method = RequestMethod.POST)
    public Object fieldModify(
            @RequestBody @Valid FieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!fieldService.checkFieldExist(requestBody.getFieldId())) { // Check if field is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (requestBody.getParentFieldId() != 0 && !fieldService.checkFieldExist(requestBody.getParentFieldId())) { // Check if parent field is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(fieldService.checkFieldSerial(requestBody.getFieldSerial(), requestBody.getFieldId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("UsedFieldSerial", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_FIELD_SERIAL);
        }
        if(fieldService.checkFieldDesignation(requestBody.getFieldDesignation(), requestBody.getFieldId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("UsedFieldDesignation", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_FIELD_DESIGNATION);
        }
        if (fieldService.checkHasChild(requestBody.getFieldId())) { // Check if field has children.
            // Can't delete if field has children.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }



        SysField sysField = requestBody.convert2SysField();
        fieldService.modifyField(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Field delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_DELETE)
    @RequestMapping(value = "/field/delete", method = RequestMethod.POST)
    public Object fieldDelete(
            @RequestBody @Valid FieldDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (fieldService.checkHasChild(requestBody.getFieldId())) { // Check if field has children.
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }
        if(fieldService.checkDeviceExist(requestBody.getFieldId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("HaveDevice", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }
        if(fieldService.checkUsedHistory(requestBody.getFieldId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("UsedField", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_FIELD);
        }
        if(fieldService.removeField(requestBody.getFieldId()) == false) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("FieldManagement.Error.ActiveField", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.ACTIVE_FIELD);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Field update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_UPDATE_STATUS)
    @RequestMapping(value = "/field/update-status", method = RequestMethod.POST)
    public Object fieldUpdateStatus(
            @RequestBody @Valid FieldUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!fieldService.checkFieldExist(requestBody.getFieldId())) { // Check if field is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Field", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(requestBody.getStatus().equals(SysField.Status.INACTIVE)) {
            if (fieldService.checkHasChild(requestBody.getFieldId())) { // Check if field has children.
                // Can't delete if field has children.
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("Field", null, currentLocale),
                        messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
            }
            if(fieldService.checkDeviceExist(requestBody.getFieldId())) { //check if device use this field
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("Field", null, currentLocale),
                        messageSource.getMessage("HaveDevice", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
            }
        }


        fieldService.updateStatus(requestBody.getFieldId(), requestBody.getStatus());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Field get all request.
     * @return
     */
    @RequestMapping(value = "/field/get-all", method = RequestMethod.POST)
    public Object fieldGetAllActive() {

        List<SysField> sysFieldList = fieldService.findAll(false); //get all list of SysField from database through fieldService
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysFieldList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

    /**
     * Field get all request.
     * @return
     */
    @RequestMapping(value = "/field/get-all-field", method = RequestMethod.POST)
    public Object fieldGetAll() {

        List<SysField> sysFieldList = fieldService.findAll(false); //get all list of SysField from database through fieldService
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysFieldList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

    /**
     * Field datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/field/get-by-filter-and-page", method = RequestMethod.POST)
    public Object fieldGetByFilterAndPage(
            @RequestBody @Valid FieldGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long parentFieldId = null;
        String status = "";
        String designation = "";
        if(requestBody.getFilter() != null) {
            parentFieldId = requestBody.getFilter().getParentFieldId(); //get field id from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            designation = requestBody.getFilter().getFieldDesignation(); //get parent field name from input parameter
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

        PageResult<SysField> result = fieldService.getDeviceListByFilter(sortBy, order, designation, status, parentFieldId, currentPage, perPage); //get list of field from database through fieldService
        long total = result.getTotal(); //get total count
        List<SysField> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set last index of current page
                        .data(data) //set data
                        .build()));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

    /**
     * Field generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_EXPORT)
    @RequestMapping(value = "/field/xlsx", method = RequestMethod.POST)
    public Object fieldGenerateExcelFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long parentFieldId = null;
        String status = "";
        String designation = "";
        if(requestBody.getFilter() != null) {
            parentFieldId = requestBody.getFilter().getParentFieldId(); //get field id from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            designation = requestBody.getFilter().getFieldDesignation(); //get parent field name from input parameter
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
        List<SysField> exportList = fieldService.getExportList(sortBy, order, designation, status, parentFieldId, requestBody.getIsAll(), requestBody.getIdList()); //get list to be exported
        setDictionary(requestBody.getLocale()); //set dictionary data
        FieldManagementExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            FieldManagementExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            FieldManagementExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = FieldManagementExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Field generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/field/docx", method = RequestMethod.POST)
    public Object fieldGenerateWordFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long parentFieldId = null;
        String status = "";
        String designation = "";
        if(requestBody.getFilter() != null) {
            parentFieldId = requestBody.getFilter().getParentFieldId(); //get field id from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            designation = requestBody.getFilter().getFieldDesignation(); //get parent field name from input parameter
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
        List<SysField> exportList = fieldService.getExportList(sortBy, order, designation, status, parentFieldId, requestBody.getIsAll(), requestBody.getIdList()); //get list to be exported
        setDictionary(requestBody.getLocale()); //set dictionary data
        FieldManagementWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            FieldManagementWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            FieldManagementWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = FieldManagementWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Field generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_FIELD_PRINT)
    @RequestMapping(value = "/field/pdf", method = RequestMethod.POST)
    public Object fieldGeneratePDFFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long parentFieldId = null;
        String status = "";
        String designation = "";
        if(requestBody.getFilter() != null) {
            parentFieldId = requestBody.getFilter().getParentFieldId(); //get field id from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            designation = requestBody.getFilter().getFieldDesignation(); //get parent field name from input parameter
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
        List<SysField> exportList = fieldService.getExportList(sortBy, order, designation, status, parentFieldId, requestBody.getIsAll(), requestBody.getIdList()); //get list to be printed

        FieldManagementPdfView.setResource(getFontResource()); //set font resource
        setDictionary(requestBody.getLocale()); //set dictionary data
        FieldManagementPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            FieldManagementPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            FieldManagementPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = FieldManagementPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
