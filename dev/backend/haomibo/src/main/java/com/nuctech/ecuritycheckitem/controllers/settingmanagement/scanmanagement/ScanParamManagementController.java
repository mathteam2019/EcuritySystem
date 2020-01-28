/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformOtherManagementController）
 * 文件名：	PlatformOtherManagementController.java
 * 描述：	Platform Other Management Controller.
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.controllers.settingmanagement.scanmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.ScanParamService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/system-setting/scan-param")
public class ScanParamManagementController extends BaseController {

    @Autowired
    ScanParamService scanParamService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;



    /**
     * Scan Param datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamByIdRequestBody {

        @NotNull
        Long paramId;
    }

    /**
     * Scan Param datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String deviceName;
            String status;
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
     * Device config delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamUpdateStatusRequestBody {

        @NotNull
        Long scanParamsId; // scan param id
        String status; //update status
    }

    /**
     * Scan Param modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ScanParamModifyRequestBody {

        @NotNull
        Long scanParamsId; // scan Params Id
        Long airCaliWarnTime; // air Cali WarnT ime
        Long standByTime; // standBy Time
        String alarmSound; // alarm Sound
        String passSound; // pass Sound
        String posErrorSound; // pos Error Sound
        String standSound; // stand Sound
        String scanSound; // scan Sound
        String scanOverUseSound; // scan Over Use Sound
        String autoRecognise; // auto Recognise
        Long recognitionRate; // recognition Rate
        String saveScanData; // save Scan Data
        String saveSuspectData; // save Suspect Data
        String facialBlurring; // facial Blurring
        String chestBlurring; // chest Blurring
        String hipBlurring; // hip Blurring
        String groinBlurring; // groin Blurring
        List<Long> fromDeviceIdList; // from deviceId List
        Integer storageAlarm; // storage alarm
        Integer storageAlarmPercent; // storage alarm percent

        SerScanParam convert2SerScanParam() { //create new object from input parameters

            return SerScanParam
                    .builder()
                    .scanParamsId(this.getScanParamsId())
                    .airCaliWarnTime(this.getAirCaliWarnTime())
                    .standByTime(this.getStandByTime())
                    .alarmSound(this.getAlarmSound())
                    .passSound(this.getPassSound())
                    .posErrorSound(this.getPosErrorSound())
                    .standSound(this.getStandSound())
                    .scanSound(this.getScanSound())
                    .scanOverUseSound(this.getScanOverUseSound())
                    .autoRecognise(this.getAutoRecognise())
                    .recognitionRate(this.getRecognitionRate())
                    .saveScanData(this.getSaveScanData())
                    .saveSuspectData(this.getSaveSuspectData())
                    .facialBlurring(this.getFacialBlurring())
                    .chestBlurring(this.getChestBlurring())
                    .hipBlurring(this.getHipBlurring())
                    .groinBlurring(this.getGroinBlurring())
                    .deviceStorageAlarm(this.getStorageAlarm())
                    .deviceStorageAlarmPercent(this.getStorageAlarmPercent())
                    .build();
        }
    }

    @RequestMapping(value = "/get-by-id", method = RequestMethod.POST)
    public Object scanParamGetById(
            @RequestBody @Valid ScanParamByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long paramId = requestBody.getParamId();
        SerScanParam serScanParam = scanParamService.getById(paramId);
        if (serScanParam == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serScanParam));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName", "field", "deviceSerial", "guid")) //return all fields except specified fields from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")); //return all fields except fieldDesignation from SysField model
        value.setFilters(filters);

        return value;
    }

    /**
     * Scan Param datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object scanParamGetByFilterAndPage(
            @RequestBody @Valid ScanParamGetByFilterAndPageRequestBody requestBody,
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

        PageResult<SerScanParam> result = scanParamService.getScanParamListByFilter(sortBy, order,
                requestBody.getFilter().getDeviceName(), //get device name from input parameters
                requestBody.getFilter().getStatus(), //get status from input parameters
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SerScanParam> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName", "field", "deviceSerial", "guid")) //return all fields except specified fields from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")); //return all fields except fieldDesignation from SysField model
        value.setFilters(filters);

        return value;
    }

    /**
     * Scan Param datatable data.
     * @return
     */
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public Object scanParamGetAll() {

        List<SerScanParam> data = scanParamService.getAllWithoutFilter();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, data));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceId", "deviceName"));
        value.setFilters(filters);

        return value;
    }

    /**
     * Scan Param modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_SCAN_PARAM_MODIFY)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object scanParamModify(
            @RequestBody @Valid ScanParamModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getScanParamsId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerScanParam serScanParamNew  = requestBody.convert2SerScanParam();
        List<Long> paramDeviceIdList = requestBody.getFromDeviceIdList();
        if (!scanParamService.modifyScanParam(paramDeviceIdList, serScanParamNew)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getScanParamsId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        else {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                    , "", "", requestBody.getScanParamsId().toString(),null);
            return new CommonResponseBody(ResponseMessage.OK);
        }
    }

    /**
     * Device Config update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object deviceConfigUpdateStatus(
            @RequestBody @Valid ScanParamUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        SerScanParam serScanParam = scanParamService.getById(requestBody.getScanParamsId()); //get scan param by i

        if(serScanParam == null) {//check device config exist or not
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        scanParamService.updateStatus(requestBody.getScanParamsId(), requestBody.getStatus()); //remove correspond manual group
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getScanParamsId().toString(),null);
        return new CommonResponseBody(ResponseMessage.OK);
    }
}
