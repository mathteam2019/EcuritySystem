/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/18
 * @CreatedBy Choe.
 * @FileName KnowledgeDealManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.knowledgemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.knowledgemanagement.KnowledgeService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeDealManagementController extends BaseController {

    @Autowired
    KnowledgeService knowledgeService;
    /**
     * Knowledge datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowLedgeDealGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String taskNumber;
            String modeName;
            String taskResult;
            String fieldDesignation;
            String handGoods;
            @Pattern(regexp = SerKnowledgeCase.Status.SUBMIT_APPROVAL + "|" + SerKnowledgeCase.Status.DISMISS
                    + "|" + SerKnowledgeCase.Status.SUCCESS_APPROVAL)
            String caseStatus;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Knowledge Case Deal update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowledgeCaseUpdateStatusRequestBody {

        @NotNull
        Long caseId;

        @NotNull
        @Pattern(regexp = SerKnowledgeCase.Status.SUBMIT_APPROVAL + "|" + SerKnowledgeCase.Status.DISMISS +
                "|" + SerKnowledgeCase.Status.SUCCESS_APPROVAL)
        String status;

    }

    /**
     * Knowledge Case Deal generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowledgeCaseGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter;
    }





    /**
     * Knowledge Case Deal datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object knowledgeDealGetByFilterAndPage(
            @RequestBody @Valid KnowLedgeDealGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();


        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        String caseStatus = "";
        String modeName = "";
        String taskNumber = "";
        String taskResult = "";
        String fieldDesignation = "";
        String handGoods = "";
        if(filter != null) {
            caseStatus = filter.getCaseStatus();
            modeName = filter.getModeName();
            taskNumber = filter.getTaskNumber();
            taskResult = filter.getTaskResult();
            fieldDesignation = filter.getFieldDesignation();
            handGoods = filter.getHandGoods();
        }
        PageResult<SerKnowledgeCaseDeal> result = knowledgeService.getDealListByFilter(caseStatus, taskNumber, modeName, taskResult,
                fieldDesignation, handGoods, currentPage, perPage);
        long total = result.getTotal();
        List<SerKnowledgeCaseDeal> data = result.getDataList();



        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SER_KNOWLEDGE_CASE, SimpleBeanPropertyFilter.filterOutAllExcept("caseStatus"))
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName"))
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl"))
                .addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskNumber"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("field", "devicePassageWay", "deviceName"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Knowledge Case update status request.
     */
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public Object knowledgeCaseUpdateStatus(
            @RequestBody @Valid KnowledgeCaseUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if knowledge case is existing.


        if (!knowledgeService.checkKnowledgeExist(requestBody.getCaseId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        knowledgeService.updateStatus(requestBody.getCaseId(), requestBody.getStatus());


        return new CommonResponseBody(ResponseMessage.OK);
    }

    private List<SerKnowledgeCaseDeal> getExportList(KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String caseStatus = "";
        String modeName = "";
        String taskNumber = "";
        String taskResult = "";
        String fieldDesignation = "";
        String handGoods = "";
        if(filter != null) {
            caseStatus = filter.getCaseStatus();
            modeName = filter.getModeName();
            taskNumber = filter.getTaskNumber();
            taskResult = filter.getTaskResult();
            fieldDesignation = filter.getFieldDesignation();
            handGoods = filter.getHandGoods();
        }
        List<SerKnowledgeCaseDeal> exportList = knowledgeService.getDealExportList(caseStatus, modeName, taskNumber, taskResult,
                fieldDesignation, handGoods, isAll, idList);
        return exportList;
    }

    /**
     * Knowledge Case pending generate excel file request.
     */
    @RequestMapping(value = "/generate/pending/export", method = RequestMethod.POST)
    public Object knowledgeCasePendingGenerateExcelFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = KnowledgeDealPendingExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case pending generate word file request.
     */
    @RequestMapping(value = "/generate/pending/word", method = RequestMethod.POST)
    public Object knowledgeCasePendingGenerateWordFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = KnowledgeDealPendingWordView.buildWordDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case pending generate pdf file request.
     */
    @RequestMapping(value = "/generate/pending/print", method = RequestMethod.POST)
    public Object knowledgeCasePendingGeneratePDFFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());

        KnowledgeDealPendingPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = KnowledgeDealPendingPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate excel file request.
     */
    @RequestMapping(value = "/generate/personal/export", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateExcelFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = KnowledgeDealPersonalExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate word file request.
     */
    @RequestMapping(value = "/generate/personal/word", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateWordFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = KnowledgeDealPersonalWordView.buildWordDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate pdf file request.
     */
    @RequestMapping(value = "/generate/personal/print", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList());
        KnowledgeDealPersonalPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = KnowledgeDealPersonalPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }


}
