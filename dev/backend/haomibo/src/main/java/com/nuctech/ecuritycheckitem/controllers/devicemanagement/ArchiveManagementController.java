/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ArchiveManagementController 1.0）
 * 文件名：	ArchiveManagementController.java
 * 描述：	Archive Management Controller
 * 作者名：	Sandy
 * 日期：	2019/10/14
 *
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchivePdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveWordView;
import com.nuctech.ecuritycheckitem.export.taskmanagement.HistoryTaskExcelView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/device-management/document-management")
public class ArchiveManagementController extends BaseController {

    @Autowired
    ArchiveService archiveService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;



    /**
     * Archive datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String archivesName; //archieve name
            String status; //status
            Long categoryId; //category id
        }

        @NotNull
        @Min(1)
        int currentPage; //current page no
        @NotNull
        int perPage; //record count per page
        Filter filter;
        String sort;
    }

    /**
     * Archive update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveUpdateStatusRequestBody {

        @NotNull
        Long archiveId; //archive id

        @NotNull
        @Pattern(regexp = SerArchive.Status.ACTIVE + "|" + SerArchive.Status.INACTIVE)
        String status;

    }

    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveCreateRequestBody {

        @NotNull
        Long archivesTemplateId;
        @NotNull
        @Size(max = 50)
        String archivesName;
        @NotNull
        @Size(max = 50)
        String archivesNumber;
        @Size(max = 500)
        String note;
        private MultipartFile imageUrl;
        String json;

        SerArchive convert2SerArchive() {
            return SerArchive
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
                    .status(SerArchive.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }

    }

    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveModifyRequestBody {

        @NotNull
        Long archiveId;
        @NotNull
        Long archivesTemplateId;
        @NotNull
        @Size(max = 50)
        String archivesName;
        @NotNull
        @Size(max = 50)
        String archivesNumber;
        @Size(max = 500)
        String note;
        private MultipartFile imageUrl;
        String json;

        SerArchive convert2SerArchive() {
            return SerArchive
                    .builder()
                    .archiveId(this.getArchiveId())
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Archive delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveDeleteRequestBody {

        @NotNull
        Long archiveId;
    }

    /**
     * Archive  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        ArchiveGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Archive datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/archive/get-by-filter-and-page", method = RequestMethod.POST)
    public Object archiveGetByFilterAndPage(
            @RequestBody @Valid ArchiveGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;

        if (requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName(); //get archive name from input paramter
            status = requestBody.getFilter().getStatus(); //get status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id from input parameter
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

        int currentPage = requestBody.getCurrentPage(); //get current page no from input parameter
        int perPage = requestBody.getPerPage(); //get records count per page from input parameter
        currentPage--;

        //get archive list from database through service
        PageResult<SerArchive> result = archiveService.getArchiveListByPage(sortBy, order, archiveName, status, categoryId,
                currentPage, perPage);

        long total = result.getTotal(); //get total count of result
        List<SerArchive> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count of result
                        .perPage(perPage) //set per page count
                        .currentPage(currentPage + 1) //set current page no
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + data.size()) //set last index of current page
                        .data(data) //set result data
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except parent from SysDeviceCategory model
        value.setFilters(filters);

        return value;
    }

    /**
     * Archive update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_UPDATE_STATUS)
    @RequestMapping(value = "/archive/update-status", method = RequestMethod.POST)
    public Object archiveUpdateStatus(
            @RequestBody @Valid ArchiveUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is existing.
        if (!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("NotArchive", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(requestBody.getStatus().equals(SerArchive.Status.INACTIVE)) {
            // Check if device is existing.
            if (archiveService.checkDeviceExist(requestBody.getArchiveId())) {
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("Archive", null, currentLocale),
                        messageSource.getMessage("HaveDevice", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
            }
        }


        archiveService.updateStatus(requestBody.getArchiveId(), requestBody.getStatus()); //update archive state to database through archiveService

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_CREATE)
    @RequestMapping(value = "/archive/create", method = RequestMethod.POST)
    public Object archiveCreate(
            @ModelAttribute @Valid ArchiveCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if template is valid
        if (!archiveService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (archiveService.checkArchiveNameExist(requestBody.getArchivesName(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ARCHIVE_NAME);
        }

        if (archiveService.checkArchiveNumberExist(requestBody.getArchivesNumber(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveNumber", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ARCHIVE_NUMBER);
        }

        SerArchive serArchive = requestBody.convert2SerArchive();
        archiveService.createSerArchive(requestBody.getImageUrl(), serArchive, requestBody.getJson()); //insert new archive to database through archiveService

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_MODIFY)
    @RequestMapping(value = "/archive/modify", method = RequestMethod.POST)
    public Object archiveModify(
            @ModelAttribute @Valid ArchiveModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check archive is valid
        if (!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("NotArchive", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if template is valid
        if (!archiveService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (archiveService.checkArchiveNameExist(requestBody.getArchivesName(), requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_ARCHIVE_NAME);
        }

        if (archiveService.checkArchiveNumberExist(requestBody.getArchivesNumber(), requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveNumber", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_ARCHIVE_NUMBER);
        }

        SerArchive serArchive = requestBody.convert2SerArchive();

        if (archiveService.checkDeviceExist(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("HaveDevice", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }
        archiveService.modifySerArchive(requestBody.getImageUrl(), serArchive, requestBody.getJson()); //modify archive to database through archiveService

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_DELETE)
    @RequestMapping(value = "/archive/delete", method = RequestMethod.POST)
    public Object archiveDelete(
            @RequestBody @Valid ArchiveDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveNumber", null, currentLocale), "", null, false, "", "");

            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check archive exist or not
        if (!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("UsedArchiveNumber", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check used device
        if (archiveService.checkDeviceExist(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("HaveDevice", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }

        if(!archiveService.removeSerArchive(requestBody.getArchiveId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Archive", null, currentLocale),
                    messageSource.getMessage("DeviceArchive.Error.ActiveArchive", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.ACTIVE_ARCHIVE);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive  get all request.
     * @return
     */
    @RequestMapping(value = "/archive/get-all", method = RequestMethod.POST)
    public Object archiveGetAll() {

        List<SerArchive> serArchiveList = archiveService.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Archive generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_EXPORT)
    @RequestMapping(value = "/archive/xlsx", method = RequestMethod.POST)
    public Object archiveGenerateExcelFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if (requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName(); //get archive name from input parameter
            status = requestBody.getFilter().getStatus(); //get archive status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id  from input parameter
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

        //get list of archives from database through archiveService
        List<SerArchive> exportList = archiveService.getExportListByFilter(sortBy, order, archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        DeviceArchiveExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchiveExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/archive/docx", method = RequestMethod.POST)
    public Object archiveGenerateWordFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if (requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName(); //get archive name from input parameter
            status = requestBody.getFilter().getStatus(); //get archive status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id  from input parameter
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

        //get list of archives from database through archiveService
        List<SerArchive> exportList = archiveService.getExportListByFilter(sortBy, order, archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        DeviceArchiveWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchiveWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_PRINT)
    @RequestMapping(value = "/archive/pdf", method = RequestMethod.POST)
    public Object archiveGeneratePDFFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if (requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName(); //get archive name from input parameter
            status = requestBody.getFilter().getStatus(); //get archive status from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id  from input parameter
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

        //get list of archives from database through archiveService
        List<SerArchive> exportList = archiveService.getExportListByFilter(sortBy, order, archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        DeviceArchivePdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        DeviceArchivePdfView.setMessageSource(messageSource);
        InputStream inputStream = DeviceArchivePdfView.buildPDFDocument(exportList);  //create inputstream of result to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
