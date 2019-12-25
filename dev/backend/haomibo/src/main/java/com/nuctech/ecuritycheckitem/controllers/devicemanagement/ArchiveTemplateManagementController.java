/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveTemplateManagementController）
 * 文件名：	ArchiveTemplateManagementController.java
 * 描述：	ArchiveTemplate Management Controller
 * 作者名：	Choe
 * 日期：	2019/11/19
 *
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplateExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplatePdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplateWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveTemplateService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/document-template")
public class ArchiveTemplateManagementController extends BaseController {

    @Autowired
    ArchiveTemplateService archiveTemplateService;

    /**
     * Archive Template datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String templateName; //template name
            String status; //template status
            Long categoryId; //category id
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Archive Template generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.

        ArchiveTemplateGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Archive Template update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateUpdateStatusRequestBody {

        @NotNull
        Long archivesTemplateId; //archive template id

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.ACTIVE + "|" + SerArchiveTemplate.Status.INACTIVE)
        String status;

    }

    /**
     * Archive Template update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorUpdateIsNullRequestBody {

        @NotNull
        Long indicatorsId; //indicator id

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.YES + "|" + SerArchiveTemplate.Status.NO)
        String isNull;

    }

    /**
     * Archive Template create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateCreateRequestBody {

        @NotNull
        String templateName; //template name
        @NotNull
        String archivesTemplateNumber; //template number
        @NotNull
        Long categoryId; //category id
        String manufacturer; //manufacturer name
        String originalModel; //original model
        String note; //note

        List<SerArchiveIndicators> archiveIndicatorsList; //archive indiciator list

        /**
         * create new object from input parameters
         * @return
         */
        SerArchiveTemplate convert2SerArchiveTemplate() {
            return SerArchiveTemplate
                    .builder()
                    .templateName(this.getTemplateName())
                    .archivesTemplateNumber(this.getArchivesTemplateNumber())
                    .categoryId(this.getCategoryId())
                    .manufacturer(Optional.ofNullable(this.getManufacturer()).orElse(""))
                    .originalModel(Optional.ofNullable(this.getOriginalModel()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getArchiveIndicatorsList())
                    .build();

        }

    }

    /**
     * Archive Indicator create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorCreateRequestBody {

        @NotNull
        String indicatorsName;
        String indicatorsUnit;

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.YES + "|" + SerArchiveTemplate.Status.NO)
        String isNull;

        SerArchiveIndicators convert2SerArchiveIndicator() { //create new object from input parameters
            return SerArchiveIndicators
                    .builder()
                    .indicatorsName(this.getIndicatorsName())
                    .indicatorsUnit(this.getIndicatorsUnit())
                    .isNull(this.getIsNull())
                    .build();

        }

    }

    /**
     * Archive Template modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateModifyRequestBody {

        @NotNull
        Long archivesTemplateId;
        @NotNull
        String templateName;
        @NotNull
        String archivesTemplateNumber;
        @NotNull
        Long categoryId;
        String manufacturer;
        String originalModel;
        String note;
        List<SerArchiveIndicators> archiveIndicatorsList;

        SerArchiveTemplate convert2SerArchiveTemplate() { //create new object from input parameters
            return SerArchiveTemplate
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .templateName(this.getTemplateName())
                    .archivesTemplateNumber(this.getArchivesTemplateNumber())
                    .categoryId(this.getCategoryId())
                    .manufacturer(Optional.ofNullable(this.getManufacturer()).orElse(""))
                    .originalModel(Optional.ofNullable(this.getOriginalModel()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getArchiveIndicatorsList())
                    .build();

        }

    }

    /**
     * Archive Template delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateDeleteRequestBody {

        @NotNull
        Long archivesTemplateId;
    }

    /**
     * Archive Indicator delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorDeleteRequestBody {

        @NotNull
        Long indicatorsId;
    }

    /**
     * Archive Template datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/archive-template/get-by-filter-and-page", method = RequestMethod.POST)
    public Object archiveTemplateGetByFilterAndPage(
            @RequestBody @Valid ArchiveTemplateGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName(); //get template name from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id from input parameter
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SerArchiveTemplate> result = archiveTemplateService.getArchiveTemplateListByPage(templateName, status, categoryId,
                currentPage, perPage); ////get list of SerArchiveTemplate from database through archiveTemplateService
        long total = result.getTotal();
        List<SerArchiveTemplate> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set current index of current page
                        .to(perPage * currentPage + data.size()) //set last index of current page
                        .data(data) //set result data
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except parent of SysDeviceCategory model

        value.setFilters(filters);

        return value;
    }

    /**
     * Archive Template generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_EXPORT)
    @RequestMapping(value = "/archive-template/xlsx", method = RequestMethod.POST)
    public Object archiveTemplateGenerateExcelFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList()); //get list of archiveTemplates from database through archiveTemplateService
        setDictionary(); //set dictionary data
        DeviceArchiveTemplateExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchiveTemplateExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/archive-template/docx", method = RequestMethod.POST)
    public Object archiveTemplateGenerateWordFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { //create inputstream of result to be exported
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName(); //get template name from input parameter
            status = requestBody.getFilter().getStatus(); //get status from from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id from input parameter
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList()); //get list of archiveTemplates from database through archiveTemplateService
        setDictionary(); //set dictionary data
        DeviceArchiveTemplateWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchiveTemplateWordView.buildWordDocument(exportList);//make input stream to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_PRINT)
    @RequestMapping(value = "/archive-template/pdf", method = RequestMethod.POST)
    public Object archiveTemplateGeneratePDFFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName(); //get template name from input parameter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id from input parameter
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList()); //get list of archiveTemplates from database through archiveTemplatesService
        DeviceArchiveTemplatePdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary date
        DeviceArchiveTemplatePdfView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchiveTemplatePdfView.buildPDFDocument(exportList); //make input stream to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_UPDATE_STATUS)
    @RequestMapping(value = "/archive-template/update-status", method = RequestMethod.POST)
    public Object archiveTemplateUpdateStatus(
            @RequestBody @Valid ArchiveTemplateUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(archiveTemplateService.checkArchiveExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        archiveTemplateService.updateStatus(requestBody.getArchivesTemplateId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicator update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_UPDATE_ISNULL)
    @RequestMapping(value = "/archive-indicator/update-isnull", method = RequestMethod.POST)
    public Object archiveIndicatorUpdateIsNull(
            @RequestBody @Valid ArchiveIndicatorUpdateIsNullRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int status = archiveTemplateService.updateIndicatorStatus(requestBody.getIndicatorsId(), requestBody.getIsNull());
        switch (status) {
            case 0:
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            case 1:
                return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_CREATE)
    @RequestMapping(value = "/archive-template/create", method = RequestMethod.POST)
    public Object archiveTemplateCreate(
            @RequestBody @Valid ArchiveTemplateCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!archiveTemplateService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(archiveTemplateService.checkTemplateNameExist(requestBody.getTemplateName(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NAME);
        }

        if(archiveTemplateService.checkTemplateNumberExist(requestBody.getArchivesTemplateNumber(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NUMBER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();
        archiveTemplateService.createSerArchiveTemplate(serArchiveTemplate);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicator create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_CREATE)
    @RequestMapping(value = "/archive-indicator/create", method = RequestMethod.POST)
    public Object archiveIndicatorCreate(
            @RequestBody @Valid ArchiveIndicatorCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchiveIndicators serArchiveIndicators = requestBody.convert2SerArchiveIndicator();

        archiveTemplateService.createArchiveIndicator(serArchiveIndicators);

        return new CommonResponseBody(ResponseMessage.OK, serArchiveIndicators.getIndicatorsId());
    }

    /**
     * Archive Template modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_MODIFY)
    @RequestMapping(value = "/archive-template/modify", method = RequestMethod.POST)
    public Object archiveTemplateModify(
            @RequestBody @Valid ArchiveTemplateModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!archiveTemplateService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check if template exist
        if(!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check this template used
        if(archiveTemplateService.checkArchiveExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        if(archiveTemplateService.checkTemplateNameExist(requestBody.getTemplateName(), requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NAME);
        }

        if(archiveTemplateService.checkTemplateNumberExist(requestBody.getArchivesTemplateNumber(), requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NUMBER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();
        archiveTemplateService.modifySerArchiveTemplate(serArchiveTemplate);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_DELETE)
    @RequestMapping(value = "/archive-template/delete", method = RequestMethod.POST)
    public Object archiveTemplateDelete(
            @RequestBody @Valid ArchiveTemplateDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check template exist or not
        if(!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(archiveTemplateService.checkArchiveExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }
        archiveTemplateService.removeSerArchiveTemplate(requestBody.getArchivesTemplateId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicators delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_DELETE)
    @RequestMapping(value = "/archive-indicator/delete", method = RequestMethod.POST)
    public Object archiveIndicatorDelete(
            @RequestBody @Valid ArchiveIndicatorDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int status = archiveTemplateService.removeSerArchiveIndicator(requestBody.getIndicatorsId());
        switch (status) {
            case 0:
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            case 1:
                return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template get all request.
     * @return
     */
    @RequestMapping(value = "/archive-template/get-all", method = RequestMethod.POST)
    public Object archiveTemplateGetAll() {


        List<SerArchiveTemplate> serArchiveTemplateList = archiveTemplateService.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveTemplateList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

}
