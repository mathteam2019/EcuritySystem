/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceControlController）
 * 文件名：	DeviceControlController.java
 * 描述：	Device Control Controller
 * 作者名：	Choe
 * 日期：	2019/11/20
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/device-management/device-table")
public class DeviceControlController extends BaseController {

    @Autowired
    DeviceService deviceService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;



    /**
     * Device datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DeviceGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Filter {
            String archivesName;
            String status;
            Long categoryId;
            String deviceName;
            Long fieldId;
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
     * Device  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        String sort;
        DeviceGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Device update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceUpdateStatusRequestBody {

        @NotNull
        Long deviceId;

        @NotNull
        @Pattern(regexp = SysDevice.Status.ACTIVE + "|" + SysDevice.Status.INACTIVE)
        String status;

    }

    /**
     * Device create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DeviceCreateRequestBody {

        @NotNull
        String deviceName;
        @NotNull
        String deviceType;
        @NotNull
        Long archiveId;
        @NotNull
        String deviceSerial;
        String originalFactoryNumber;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date manufacturerDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date purchaseDate;
        String supplier;
        String contacts;
        String mobile;
        String deviceIp;
        String guid;
        String note;

        private MultipartFile imageUrl;

        SysDevice convert2SysDevice() { //create new object from input parameters
            return SysDevice
                    .builder()
                    .archiveId(this.getArchiveId())
                    .deviceName(this.getDeviceName())
                    .deviceType(this.getDeviceType())
                    .deviceSerial(this.getDeviceSerial())
                    .originalFactoryNumber(Optional.ofNullable(this.getOriginalFactoryNumber()).orElse(""))
                    .manufacturerDate(this.getManufacturerDate())
                    .purchaseDate(this.getPurchaseDate())
                    .supplier(Optional.ofNullable(this.getSupplier()).orElse(""))
                    .contacts(Optional.ofNullable(this.getContacts()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .deviceIp(Optional.ofNullable(this.getDeviceIp()).orElse(""))
                    .guid(this.getGuid())
                    .status(SysDevice.Status.INACTIVE)
                    .currentStatus(SysDevice.DeviceStatus.UNREGISTER)
                    .workStatus(SysDevice.DeviceWorkStatus.FREE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Device config modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DeviceModifyRequestBody {

        @NotNull
        Long deviceId;
        @NotNull
        String deviceName;
        @NotNull
        String deviceType;
        @NotNull
        Long archiveId;
        @NotNull
        String deviceSerial;
        String originalFactoryNumber;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date manufacturerDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date purchaseDate;
        String supplier;
        String contacts;
        String mobile;
        String deviceIp;
        String guid;
        String note;

        private MultipartFile imageUrl;

        SysDevice convert2SysDevice() { //create new object from input parameter
            return SysDevice
                    .builder()
                    .deviceId(this.getDeviceId())
                    .archiveId(this.getArchiveId())
                    .deviceName(this.getDeviceName())
                    .deviceType(this.getDeviceType())
                    .deviceSerial(this.getDeviceSerial())
                    .originalFactoryNumber(Optional.ofNullable(this.getOriginalFactoryNumber()).orElse(""))
                    .manufacturerDate(this.getManufacturerDate())
                    .purchaseDate(this.getPurchaseDate())
                    .supplier(Optional.ofNullable(this.getSupplier()).orElse(""))
                    .contacts(Optional.ofNullable(this.getContacts()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .deviceIp(Optional.ofNullable(this.getDeviceIp()).orElse(""))
                    .guid(this.getGuid())
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Device delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceDeleteRequestBody {

        @NotNull
        Long deviceId;
    }

    /**
     * Device field modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceFieldModifyRequestBody {

        List<SysDevice> deviceList;
    }

    /**
     * Organization get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_CONFIG = "with_config";
            static final String WITH_SCAN = "with_scan";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_CONFIG + "|" +
                GetAllType.WITH_SCAN)
        String type = GetAllType.BARE;
    }

    /**
     * Empty field device list request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGetEmptyFieldRequestBody {
        Long categoryId;
    }

    /**
     * Empty field device list request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceFilterByFieldRequestBody {
        Long categoryId;
        Long fieldId;
    }

    /**
     * Device datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/device/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceGetByFilterAndPage(
            @RequestBody @Valid DeviceGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();


        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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
        PageResult<SysDevice> result = deviceService.getFilterDeviceList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId, perPage, currentPage); //get list of devices from database through deviceService
        List<SysDevice> data = result.getDataList();
        long total = result.getTotal();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, // set response message as OK
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

        // Set filters.
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except parent from SysDeviceCategory model
        value.setFilters(filters);

        return value;
    }

    /**
     * Device generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_EXPORT)
    @RequestMapping(value = "/device/xlsx", method = RequestMethod.POST)
    public Object deviceGenerateExcelFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of device to be exported
        setDictionary(); //set dictionary data
        DeviceExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/device/docx", method = RequestMethod.POST)
    public Object deviceGenerateWordFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of device to be exported
        setDictionary(); //set dictionary data
        DeviceWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_PRINT)
    @RequestMapping(value = "/device/pdf", method = RequestMethod.POST)
    public Object deviceGeneratePDFFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList());  //get list of device to be exported
        DevicePdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        DevicePdfView.setMessageSource(messageSource);
        InputStream inputStream = DevicePdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device generate excel file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_EXPORT)
    @RequestMapping(value = "/device/field/xlsx", method = RequestMethod.POST)
    public Object deviceFieldGenerateExcelFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of device to be exported
        setDictionary(); //set dictionary data
        DeviceFieldExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceFieldExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-field.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device generate word file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_EXPORT)
    @RequestMapping(value = "/device/field/docx", method = RequestMethod.POST)
    public Object deviceFieldGenerateWordFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of device to be exported
        setDictionary(); //set dictionary data
        DeviceFieldWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceFieldWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-field.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_PRINT)
    @RequestMapping(value = "/device/field/pdf", method = RequestMethod.POST)
    public Object deviceFieldGeneratePDFFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName(); //get archive name from input parameter
            deviceName = filter.getDeviceName(); //get device name from input parameter
            status = filter.getStatus(); //get status from input parameter
            fieldId = filter.getFieldId(); //get field id from input parameter
            categoryId = filter.getCategoryId(); //get category id from input parameter
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

        List<SysDevice> exportList = deviceService.getExportDataList(sortBy, order, archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of device to be exported
        DeviceFieldPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        DeviceFieldPdfView.setMessageSource(messageSource);
        InputStream inputStream = DeviceFieldPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-field.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_UPDATE_STATUS)
    @RequestMapping(value = "/device/update-status", method = RequestMethod.POST)
    public Object deviceUpdateStatus(
            @RequestBody @Valid DeviceUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (!deviceService.checkDeviceExist(requestBody.getDeviceId())) {// Check if device is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(requestBody.getStatus().equals(SysDevice.Status.INACTIVE)) {
            if (deviceService.checkDeviceContainField(requestBody.getDeviceId())) {// Check if device contain field
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("Device", null, currentLocale),
                        messageSource.getMessage("HaveField", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.HAS_FIELDS);
            }

            int configSettingResult = deviceService.checkDeviceConfigActive(requestBody.getDeviceId());
            if(configSettingResult == 1) {
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("Device", null, currentLocale),
                        messageSource.getMessage("DeviceConfigActive", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.DEVICE_CONFIG_ACTIVE);
            }
//            else if(configSettingResult == 2) {
//                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
//                        , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getDeviceId().toString(),null);
//                return new CommonResponseBody(ResponseMessage.DEVICE_SCAN_ACTIVE);
//            }
        }
        deviceService.updateStatus(requestBody.getDeviceId(), requestBody.getStatus());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CREATE)
    @RequestMapping(value = "/device/create", method = RequestMethod.POST)
    public Object deviceCreate(
            @ModelAttribute @Valid DeviceCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!deviceService.checkArchiveExist(requestBody.getArchiveId())) { // Check if archive is valid.
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(deviceService.checkDeviceNameExist(requestBody.getDeviceName(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DEVICE_NAME);
        }
        if(deviceService.checkDeviceSerialExist(requestBody.getDeviceSerial(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceSerial", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DEVICE_SERIAL);
        }
        if(deviceService.checkDeviceGuidExist(requestBody.getGuid(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceGuid", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DEVICE_GUID);
        }
        SysDevice sysDevice = requestBody.convert2SysDevice();
        deviceService.createDevice(sysDevice, requestBody.getImageUrl());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_MODIFY)
    @RequestMapping(value = "/device/modify", method = RequestMethod.POST)
    public Object deviceModify(
            @ModelAttribute @Valid DeviceModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!deviceService.checkArchiveExist(requestBody.getArchiveId())) {        // Check if archive is valid.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (!deviceService.checkDeviceExist(requestBody.getDeviceId())) {        // Check if device is valid.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(deviceService.checkDeviceNameExist(requestBody.getDeviceName(), requestBody.getDeviceId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DEVICE_NAME);
        }
        if(deviceService.checkDeviceSerialExist(requestBody.getDeviceSerial(), requestBody.getDeviceId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceSerial", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DEVICE_SERIAL);
        }
        if(deviceService.checkDeviceGuidExist(requestBody.getGuid(), requestBody.getDeviceId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDeviceGuid", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DEVICE_GUID);
        }

        SysDevice sysDevice = requestBody.convert2SysDevice();
        deviceService.modifyDevice(sysDevice, requestBody.getImageUrl());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_DELETE)
    @RequestMapping(value = "/device/delete", method = RequestMethod.POST)
    public Object deviceDelete(
            @RequestBody @Valid DeviceDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if(!deviceService.checkDeviceExist(requestBody.getDeviceId())) { //check device exist or not
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int checkStatus = deviceService.checkDeviceStatus(requestBody.getDeviceId());


        if(checkStatus == 1) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("UsedDevice", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DEVICE);
        }

        if(!deviceService.removeDevice(requestBody.getDeviceId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("Device.Error.ActiveDevice", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.ACTIVE_DEVICE);

        }
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device field modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_MODIFY)
    @RequestMapping(value = "/device/field-modify", method = RequestMethod.POST)
    public Object deviceFieldModify(
            @RequestBody @Valid DeviceFieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Device", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> deviceList = requestBody.getDeviceList();
        deviceService.modifyDeviceField(deviceList);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device  get all request.
     * BARE, WITH_CONFIG, WITH_SCAN
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/device/get-all", method = RequestMethod.POST)
    public Object deviceGetAll(@RequestBody @Valid DeviceGetAllRequestBody requestBody,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> sysDeviceList = deviceService.findAll();
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        String type = requestBody.getType();
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        switch (type) {
            case DeviceGetAllRequestBody.GetAllType.BARE: //case of bare
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except specified parent from SysDeviceCategory model
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_CONFIG: //case of with config
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("scanParam"))//return all fields except specified fields from SysDevice model
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CONFIG, SimpleBeanPropertyFilter.serializeAllExcept("device")) //return all fields except device from SysDeviceConfig model
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except specified parent from SysDeviceCategory model
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_SCAN: //case of with scan
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig")) //return all fields except deviceConfig from SysDevice model
                        .addFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAM, SimpleBeanPropertyFilter.serializeAllExcept("device")) //return all fields except device  from SerScanParam model
                        .addFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAMS_FROM, SimpleBeanPropertyFilter.serializeAllExcept("device")) //return all fields except device  from SerScanParamsFrom model
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except  parent from SysDeviceCategory model
                break;
            default:
                break;
        }
        value.setFilters(filters);

        return value;
    }

    /**
     * Get empty field device request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/device/get-empty-field", method = RequestMethod.POST)
    public Object deviceGetEmptyField(@RequestBody @Valid DeviceGetEmptyFieldRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> sysDeviceList = deviceService.getEmptyFieldDevice(requestBody.getCategoryId()); //get device list from database through deviceService

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except  parent from SysDeviceCategory model
        value.setFilters(filters);

        return value;
    }

    /**
     * Get device list with field request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/device/get-by-field", method = RequestMethod.POST)
    public Object deviceGetByField(@RequestBody @Valid DeviceFilterByFieldRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> sysDeviceList = deviceService.getDeviceByField(requestBody.getFieldId(), requestBody.getCategoryId()); //get device list from database through deviceService
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except  parent from SysDeviceCategory model

        value.setFilters(filters);

        return value;
    }

}
