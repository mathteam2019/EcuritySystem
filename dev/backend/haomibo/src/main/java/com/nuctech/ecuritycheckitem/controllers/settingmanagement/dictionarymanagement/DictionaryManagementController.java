/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DictionaryManagementController）
 * 文件名：	DictionaryManagementController.java
 * 描述：	Dictionary management controller.
 * 作者名：	Choe
 * 日期：	2020/01/10
 */

package com.nuctech.ecuritycheckitem.controllers.settingmanagement.dictionarymanagement;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerSeizedGood;
import com.nuctech.ecuritycheckitem.models.db.SysDictionary;
import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.DictionaryService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dictionary management controller.
 */
@RestController
@RequestMapping("/dictionary-management")
public class DictionaryManagementController extends BaseController {

    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    AuditLogService auditLogService;



    /**
     * Dictionary datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String dictionaryName;
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
     * Dictionary data datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDataGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String dataCode;
            String dataValue;
            Long dictionaryId;
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
     * Dictionary data by dictionary id.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDataGetByIdRequestBody {

        @NotNull
        Long dictionaryId;
    }

    /**
     * Dictionary modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryModifyRequestBody {

        @NotNull
        Long dictionaryId;
        @NotNull
        @Size(max = 50)
        String dictionaryName;
        @Size(max = 500)
        String note;
    }

    /**
     * Dictionary data modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDataModifyRequestBody {

        @NotNull
        Long dataId;
        @NotNull
        Long dictionaryId;
        @NotNull
        @Size(max = 10)
        String dataCode;
        @NotNull
        @Size(max = 200)
        String dataValue;
        @Size(max = 500)
        String note;
    }

    /**
     * Dictionary Create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryCreateRequestBody {

        @NotNull
        @Size(max = 50)
        String dictionaryName;
        @Size(max = 500)
        String note;
    }

    /**
     * DictionaryData Create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDataCreateRequestBody {

        @NotNull
        Long dictionaryId;
        @NotNull
        @Size(max = 10)
        String dataCode;
        @NotNull
        @Size(max = 200)
        String dataValue;

        @Size(max = 500)
        String note;
    }

    /**
     * Dictionary delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDeleteRequestBody {
        @NotNull
        Long dictionaryId;
    }

    /**
     * Dictionary data delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DictionaryDataDeleteRequestBody {
        @NotNull
        Long dataId;
    }

    /**
     * Dictionary create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_CREATE)
    @RequestMapping(value = "/dictionary/create", method = RequestMethod.POST)
    public Object dictionaryCreate(
            @RequestBody @Valid DictionaryCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(dictionaryService.checkDictionary(requestBody.getDictionaryName(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryName", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_NAME);
        }

        SysDictionary sysDictionary = SysDictionary.builder()
                .dictionaryName(requestBody.getDictionaryName())
                .dictionaryType(SysDictionary.Type.USER)
                .note(requestBody.getNote())
                .build();
        dictionaryService.createDictionary(sysDictionary);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Dictionary create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_CREATE)
    @RequestMapping(value = "/dictionary-data/create", method = RequestMethod.POST)
    public Object dictionaryDataCreate(
            @RequestBody @Valid DictionaryDataCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!dictionaryService.checkDictionaryExist(requestBody.getDictionaryId())) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(dictionaryService.checkDictionaryData(requestBody.getDataValue(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryValue", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_VALUE);
        }

        if(dictionaryService.checkDictionaryDataCode(requestBody.getDataCode(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryCode", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_CODE);
        }

        SysDictionaryData sysDictionaryData = SysDictionaryData.builder()
                .dictionaryId(requestBody.getDictionaryId())
                .dataCode(requestBody.getDataCode())
                .dataValue(requestBody.getDataValue())
                .note(requestBody.getNote())
                .build();
        dictionaryService.createDictionaryData(sysDictionaryData);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Dictionary modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_MODIFY)
    @RequestMapping(value = "/dictionary/modify", method = RequestMethod.POST)
    public Object dictionaryModify(
            @RequestBody @Valid DictionaryModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        if (!dictionaryService.checkDictionaryExist(requestBody.getDictionaryId())) { // Check if goods id is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (dictionaryService.checkDictionaryChildExist(requestBody.getDictionaryId())) { // Check if goods id is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        if(dictionaryService.checkDictionary(requestBody.getDictionaryName(), requestBody.getDictionaryId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryName", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_VALUE);
        }


        SysDictionary sysDictionary = SysDictionary.builder()
                .dictionaryName(requestBody.getDictionaryName())
                .dictionaryId(requestBody.getDictionaryId())
                .dictionaryType(SysDictionary.Type.USER)
                .note(requestBody.getNote())
                .build();
        dictionaryService.modifyDictionary(sysDictionary);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Dictionary data modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_MODIFY)
    @RequestMapping(value = "/dictionary-data/modify", method = RequestMethod.POST)
    public Object dictionaryDataModify(
            @RequestBody @Valid DictionaryDataModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        if (!dictionaryService.checkDictionaryDataExist(requestBody.getDataId())) { // Check if data id is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(dictionaryService.checkDictionaryDataCode(requestBody.getDataCode(), requestBody.getDataId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryCode", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_CODE);
        }

        if(dictionaryService.checkDictionaryData(requestBody.getDataValue(), requestBody.getDataId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("UsedDictionaryValue", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.USED_DICTIONARY_VALUE);
        }


        SysDictionaryData sysDictionaryData = SysDictionaryData.builder()
                .dictionaryId(requestBody.getDictionaryId())
                .dataValue(requestBody.getDataValue())
                .dataCode(requestBody.getDataCode())
                .dataId(requestBody.getDataId())
                .note(requestBody.getNote())
                .build();
        dictionaryService.modifyDictionaryData(sysDictionaryData);


        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Dictionary delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_DELETE)
    @RequestMapping(value = "/dictionary/delete", method = RequestMethod.POST)
    public Object dictionaryDelete(
            @RequestBody @Valid DictionaryDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (dictionaryService.checkDictionaryChildExist(requestBody.getDictionaryId())) { // Check if goods id is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("Dictionary", null, currentLocale),
                    messageSource.getMessage("HaveChild", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        dictionaryService.removeDictionary(requestBody.getDictionaryId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Dictionary data delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_DELETE)
    @RequestMapping(value = "/dictionary-data/delete", method = RequestMethod.POST)
    public Object dictionaryDataDelete(
            @RequestBody @Valid DictionaryDataDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DictionaryData", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        dictionaryService.removeDictionaryData(requestBody.getDataId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Dictionay datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/dictionary/get-by-filter-and-page", method = RequestMethod.POST)
    public Object dictionaryGetByFilterAndPage(
            @RequestBody @Valid DictionaryGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String dictionaryName = "";
        if(requestBody.getFilter() != null) {
            dictionaryName = requestBody.getFilter().getDictionaryName();
        }
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();
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
        PageResult<SysDictionary> result = dictionaryService.getDictionaryListByFilter(sortBy, order, dictionaryName, currentPage, perPage); //get list of field from database through fieldService
        long total = result.getTotal(); //get total count
        List<SysDictionary> data = result.getDataList();

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
     * Dictionay datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/dictionary-data/get-by-filter-and-page", method = RequestMethod.POST)
    public Object dictionaryDataGetByFilterAndPage(
            @RequestBody @Valid DictionaryDataGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String dataValue = "";
        String dataCode = "";
        Long dictionaryId = null;
        if(requestBody.getFilter() != null) {
            dictionaryId = requestBody.getFilter().getDictionaryId();
            dataValue = requestBody.getFilter().getDataValue();
            dataCode = requestBody.getFilter().getDataCode();
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

        PageResult<SysDictionaryData> result = dictionaryService.getDictionaryDataListByFilter(sortBy, order, dataCode,
                dataValue, dictionaryId, currentPage, perPage); //get list of field from database through fieldService
        long total = result.getTotal(); //get total count
        List<SysDictionaryData> data = result.getDataList();

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
     * Dictionay datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/dictionary-data/get-by-id", method = RequestMethod.POST)
    public Object dictionaryDataGetById(
            @RequestBody @Valid DictionaryDataGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysDictionaryData> dictionaryDataList = dictionaryService.getDictionaryListById(requestBody.getDictionaryId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, dictionaryDataList));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters();

        value.setFilters(filters);

        return value;
    }






}
