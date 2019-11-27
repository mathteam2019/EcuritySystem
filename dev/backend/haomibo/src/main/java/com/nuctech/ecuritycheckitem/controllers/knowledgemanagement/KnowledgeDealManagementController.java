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
import com.nuctech.ecuritycheckitem.excel.knowledgemanagement.KnowledgeDealPendingExcelView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeDealManagementController extends BaseController {
    /**
     * Field datatable request body.
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
     * Knowledge Case Deal update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowledgeCaseGenerateRequestBody {

        @NotNull
        String idList;

        @NotNull
        Boolean isAll;


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


        QSerKnowledgeCaseDeal builder = QSerKnowledgeCaseDeal.serKnowledgeCaseDeal;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            predicate.and(builder.knowledgeCase.caseStatus.eq(filter.getCaseStatus()));
            if (!StringUtils.isEmpty(filter.getTaskNumber())) {
                predicate.and(builder.task.taskNumber.contains(filter.getTaskNumber()));
            }
            if (!StringUtils.isEmpty(filter.getModeName())) {
                predicate.and(builder.workMode.modeName.eq(filter.getModeName()));
            }

            if (!StringUtils.isEmpty(filter.getTaskResult())) {
                predicate.and(builder.handTaskResult.eq(filter.getTaskResult()));
            }
            if (!StringUtils.isEmpty(filter.getFieldDesignation())) {
                predicate.and(builder.scanDevice.field.fieldDesignation.contains(filter.getFieldDesignation()));
            }

            if (!StringUtils.isEmpty(filter.getHandGoods())) {
                predicate.and(builder.handGoods.contains(filter.getHandGoods()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serKnowledgeCaseDealRepository.count(predicate);
        List<SerKnowledgeCaseDeal> data = serKnowledgeCaseDealRepository.findAll(predicate, pageRequest).getContent();



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
        Optional<SerKnowledgeCase> optionalSerKnowledgeCase = serKnowledgeCaseRepository.findOne(QSerKnowledgeCase.serKnowledgeCase
                .caseId.eq(requestBody.getCaseId()));
        if (!optionalSerKnowledgeCase.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerKnowledgeCase serKnowledgeCase = optionalSerKnowledgeCase.get();

        // Update status.
        serKnowledgeCase.setCaseStatus(requestBody.getStatus());

        // Add edited info.
        serKnowledgeCase.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serKnowledgeCaseRepository.save(serKnowledgeCase);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Knowledge Case generate excel request.
     */
    @RequestMapping(value = "/generate/pending/excel", method = RequestMethod.GET)
    public Object knowledgeCasePendingGenerateExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Workbook workbook = null;
        byte[] contentReturn=null;
        /* Here I got the object structure (pulling it from DAO layer) that I want to be export as part of Excel. */
        List<SerKnowledgeCaseDeal> dealList = serKnowledgeCaseDealRepository.findAll();
        try{
            /* Logic to Export Excel */
            String fileName = "knowledge-pending.xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            OutputStream out;
            workbook = new KnowledgeDealPendingExcelView().buildExcelDocument(dealList);
            out = response.getOutputStream();
            workbook.write(out);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            contentReturn = baos.toByteArray();
            workbook.close();
            /* Export Excel logic end */

        } catch (Exception ecx) {

        }

        ByteArrayResource resource = new ByteArrayResource(contentReturn);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contentReturn.length)
                //.contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);




        //return new ResponseEntity<byte[]>(contentReturn, headers, HttpStatus.OK);
    }

}
