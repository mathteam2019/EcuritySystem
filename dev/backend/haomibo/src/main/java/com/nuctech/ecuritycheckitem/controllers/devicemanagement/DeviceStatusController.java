/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceStatusController）
 * 文件名：	DeviceStatusController.java
 * 描述：	Device Status Controller
 * 作者名：	Choe
 * 日期：	2019/11/20
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceStatusService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/device-management/condition-monitoring")
public class DeviceStatusController extends BaseController {

    @Autowired
    DeviceStatusService deviceStatusService;
    /**
     * Device status datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceStatusGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            Long fieldId;
            Long categoryId;
            String deviceName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceStatusGetByIdRequestBody {
        @NotNull
        Long statusId;
    }

    /**
     * Device datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceGetByFilterAndPage(
            @RequestBody @Valid DeviceStatusGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long fieldId = null;
        Long categoryId = null;
        String deviceName = "";

        if(requestBody.getFilter() != null) {
            fieldId = requestBody.getFilter().getFieldId(); //get field id from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get category id from input parameter
            deviceName = requestBody.getFilter().getDeviceName(); //get device name from input parameter
        }
        int currentPage = requestBody.getCurrentPage() - 1;
        int perPage = requestBody.getPerPage();
        //get SerDeviceStatus list from database through deviceStatusService
        PageResult<SerDeviceStatus> result = deviceStatusService.getDeviceStatusByFilter(fieldId, deviceName, categoryId, currentPage, perPage);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(result.getTotal()) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) result.getTotal()) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + result.getDataList().size()) //set last index of current page
                        .data(result.getDataList()) //get data list
                        .build()));
        // Set filters.
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SER_DEVICE_STATUS, SimpleBeanPropertyFilter.serializeAllExcept("serScanParam", "scanList"));   //return all fields except specified fields from SysDevice model

        value.setFilters(filters);

        return value;
    }

    /**
     * Device datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-id", method = RequestMethod.POST)
    public Object deviceGetById(
            @RequestBody @Valid DeviceStatusGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerDeviceStatus status = deviceStatusService.getDeviceStatusById(requestBody.getStatusId());
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, status));
        // Set filters.
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SER_DEVICE_STATUS, SimpleBeanPropertyFilter.serializeAllExcept("serScanParamList", "scanList"))   //return all fields except specified fields from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"));  //return all fields except parent from SysField model


        value.setFilters(filters);

        return value;
    }


}
