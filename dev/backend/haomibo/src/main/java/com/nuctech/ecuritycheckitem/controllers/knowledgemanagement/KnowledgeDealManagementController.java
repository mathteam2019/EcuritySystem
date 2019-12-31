/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealManagementController）
 * 文件名：	KnowledgeDealManagementController.java
 * 描述：	KnowledgeDeal Management Controller.
 * 作者名：	Choe
 * 日期：	2019/11/18
 */

package com.nuctech.ecuritycheckitem.controllers.knowledgemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingPdfView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingExcelView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingWordView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalPdfView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalExcelView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCase;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;
import com.nuctech.ecuritycheckitem.service.knowledgemanagement.KnowledgeService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
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
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeDealManagementController extends BaseController {

    @Autowired
    KnowledgeService knowledgeService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

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

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * new Knowledge Case insert request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowledgeCaseInsertRequestBody {

        @NotNull
        Long historyId;
        @NotNull
        Long userId;
    }

    //@PreAuthorize(Role.Authority.HAS_KNOWLEDGECASE_CREATE)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object insertKnowledgeCase(@RequestBody @Valid KnowledgeCaseInsertRequestBody requestBody, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getHistoryId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        HistorySimplifiedForHistoryTaskManagement history = historyService.getOne(requestBody.getHistoryId());
        if (history == null) { //if specified history id doesn't exist
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getHistoryId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //prepare knowledgecase object to insert
        SerKnowledgeCase knowledgeCase = new SerKnowledgeCase();
        knowledgeCase.setTaskId(history.getTaskId());
        knowledgeCase.setCaseStatus(SerKnowledgeCase.Status.SUBMIT_APPROVAL);
        knowledgeCase.setCaseCollectUserId(requestBody.getUserId());

        //insert new knowledge case and get new id
        Long knowledgeId = knowledgeService.insertNewKnowledgeCase(knowledgeCase);
        if (knowledgeId == null) { //failed inserting
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getHistoryId().toString(),null);
            return new CommonResponseBody(ResponseMessage.FAILED_INSERT_KNOWLEDGECASE);
        }
        knowledgeCase.setCaseId(knowledgeId);

        //prepare new knowledgecasedeal to insert
        SerKnowledgeCaseDeal knowledgeCaseDeal = initSerKnowCaseDealFromHistory(history);
        knowledgeCaseDeal.setCaseId(knowledgeId);
        Long knowledgeCaseDealId = knowledgeService.insertNewKnowledgeCaseDeal(knowledgeCaseDeal);
        if (knowledgeCaseDealId == null) { //failed inserting
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getHistoryId().toString(),null);
            return new CommonResponseBody(ResponseMessage.FAILED_INSERT_KNOWLEDGECASEDEAL);
        }
        knowledgeCase.setCaseDealId(knowledgeCaseDealId); //set new knowledgecasedeal id to knowledgecase and update it
        if (knowledgeService.updateKnowledgeCase(knowledgeId, knowledgeCase) == null) { //failed updating
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getHistoryId().toString(),null);
            return new CommonResponseBody(ResponseMessage.FAILED_UPDATE_KNOWLEDGECASE);
        }
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getHistoryId().toString(),null);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    private SerKnowledgeCaseDeal initSerKnowCaseDealFromHistory(HistorySimplifiedForHistoryTaskManagement history) {

        SerKnowledgeCaseDeal serKnowledgeCaseDeal = new SerKnowledgeCaseDeal();

        serKnowledgeCaseDeal.setTaskId(history.getTaskId());
        serKnowledgeCaseDeal.setMode(history.getMode());
        serKnowledgeCaseDeal.setScanId(history.getScanId());
        serKnowledgeCaseDeal.setScanWorkflowId(history.getScanWorkflowId());
        serKnowledgeCaseDeal.setScanDeviceId(history.getScanDeviceId());
        serKnowledgeCaseDeal.setScanImageId(history.getScanImageId());
        serKnowledgeCaseDeal.setScanAtrResult(history.getScanAtrResult());
        serKnowledgeCaseDeal.setScanFootAlarm(history.getScanFootAlarm());
        serKnowledgeCaseDeal.setScanStartTime(history.getScanStartTime());
        serKnowledgeCaseDeal.setScanEndTime(history.getScanEndTime());
        serKnowledgeCaseDeal.setScanPointsmanId(history.getScanPointsmanId());
        serKnowledgeCaseDeal.setScanPointsmanName(history.getScanPointsmanName());
//        serKnowledgeCaseDeal.setAssignscanId(history.getAssignScanId());
//        serKnowledgeCaseDeal.setAssignWorkflowId(history.getAssignWorkflowId());
//        serKnowledgeCaseDeal.setAssignUserId(history.getAssignUserId());
//        serKnowledgeCaseDeal.setAssignUserName(history.getAssignUserName());
        serKnowledgeCaseDeal.setAssignJudgeDeviceId(history.getAssignJudgeDeviceId());
        serKnowledgeCaseDeal.setAssignHandDeviceId(history.getAssignHandDeviceId());
//        serKnowledgeCaseDeal.setAssignStartTime(history.getAssignStartTime());
//        serKnowledgeCaseDeal.setAssignEndTime(history.getAssignEndTime());
//        serKnowledgeCaseDeal.setAssignTimeout(history.getAssignTimeout());
//        serKnowledgeCaseDeal.setAssignStatus(history.getAssignStatus());
        serKnowledgeCaseDeal.setJudgeId(history.getJudgeId());
        serKnowledgeCaseDeal.setJudgeWorkflowId(history.getJudgeWorkflowId());
        serKnowledgeCaseDeal.setJudgeDeviceId(history.getJudgeDeviceId());
        serKnowledgeCaseDeal.setJudgeResult(history.getJudgeResult());
        serKnowledgeCaseDeal.setJudgeTimeout(history.getJudgeTimeout());
        serKnowledgeCaseDeal.setHandExaminationId(history.getHandExaminationId());
        serKnowledgeCaseDeal.setHandWorkflowId(history.getHandWorkflowId());
        serKnowledgeCaseDeal.setHandDeviceId(history.getHandDeviceId());
        serKnowledgeCaseDeal.setHandResult(history.getHandResult());
        serKnowledgeCaseDeal.setHandStartTime(history.getHandStartTime());
        serKnowledgeCaseDeal.setHandEndTime(history.getHandEndTime());
        serKnowledgeCaseDeal.setHandUserId(history.getHandUserId());
        serKnowledgeCaseDeal.setHandTaskResult(history.getHandTaskResult());
        serKnowledgeCaseDeal.setHandGoods(history.getHandGoods());
        serKnowledgeCaseDeal.setHandGoodsGrade(history.getHandGoodsGrade());
        serKnowledgeCaseDeal.setHandCollectSign(history.getHandCollectSign());
        //serKnowledgeCaseDeal.setHandAttachedId(history.getHandAttached());
        serKnowledgeCaseDeal.setHandCollectLabel(history.getHandCollectLabel());
        serKnowledgeCaseDeal.setHandAppraise(history.getHandAppraise());
        serKnowledgeCaseDeal.setJudgeStartTime(history.getJudgeStartTime());
        serKnowledgeCaseDeal.setJudgeEndTime(history.getJudgeEndTime());
        serKnowledgeCaseDeal.setJudgeUserId(history.getJudgeUserId());
        serKnowledgeCaseDeal.setJudgeAssignTimeout(history.getJudgeAssignTimeout());
        serKnowledgeCaseDeal.setJudgeStatus(history.getJudgeStatus());

        return serKnowledgeCaseDeal;
    }

    /**
     * Knowledge Case Deal datatable data.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object knowledgeDealGetByFilterAndPage(
            @RequestBody @Valid KnowLedgeDealGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
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
        if (filter != null) {
            caseStatus = filter.getCaseStatus(); //get case status from input parameter
            modeName = filter.getModeName(); //get mode name from input parameter
            taskNumber = filter.getTaskNumber(); //get task number from input parameter
            taskResult = filter.getTaskResult(); //get task result from input parameter
            fieldDesignation = filter.getFieldDesignation(); //get field name from input parameter
            handGoods = filter.getHandGoods(); //get handgoods from input parameter
        }
        PageResult<SerKnowledgeCaseDeal> result = knowledgeService.getDealListByFilter(caseStatus, taskNumber, modeName, taskResult,
                fieldDesignation, handGoods, currentPage, perPage); //get result from database through service
        long total = result.getTotal();
        List<SerKnowledgeCaseDeal> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SER_KNOWLEDGE_CASE, SimpleBeanPropertyFilter.filterOutAllExcept("caseStatus"))  //return all fields except caseStatus from SerKnowLedgeCase model
                .addFilter(ModelJsonFilters.FILTER_SYS_WORK_MODE, SimpleBeanPropertyFilter.filterOutAllExcept("modeName")) //return all fields except modeName from  SysWorkMode model
                .addFilter(ModelJsonFilters.FILTER_SER_IMAGE, SimpleBeanPropertyFilter.filterOutAllExcept("imageUrl")) //return all fields except imageUrl from  SerImage model
                .addFilter(ModelJsonFilters.FILTER_SER_TASK, SimpleBeanPropertyFilter.filterOutAllExcept("taskNumber")) //return all fields except taskNumber from  SerTask model
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("field", "devicePassageWay", "deviceName")) //return all fields except specified fieldds from SysDevice model
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.filterOutAllExcept("fieldDesignation")); //return all fields except " from  SysField model
        value.setFilters(filters);

        return value;
    }

    /**
     * Knowledge Case update status request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public Object knowledgeCaseUpdateStatus(
            @RequestBody @Valid KnowledgeCaseUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getCaseId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!knowledgeService.checkKnowledgeExist(requestBody.getCaseId())) { // Check if knowledge case is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getCaseId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        knowledgeService.updateStatus(requestBody.getCaseId(), requestBody.getStatus()); //update db through knowledgeService
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getCaseId().toString(),null);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * get list of data to be exported
     *
     * @param filter
     * @param isAll
     * @param idList
     * @return
     */
    private List<SerKnowledgeCaseDeal> getExportList(KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String caseStatus = "";
        String modeName = "";
        String taskNumber = "";
        String taskResult = "";
        String fieldDesignation = "";
        String handGoods = "";
        if (filter != null) {
            caseStatus = filter.getCaseStatus(); //get case status from input parameter
            modeName = filter.getModeName(); //get mode name from input parameter
            taskNumber = filter.getTaskNumber(); //get task number from input parameter
            taskResult = filter.getTaskResult(); //get task result from input parameter
            fieldDesignation = filter.getFieldDesignation(); //get field name from input parameter
            handGoods = filter.getHandGoods(); //get handgoods from input parameter
        }
        List<SerKnowledgeCaseDeal> exportList = knowledgeService.getDealExportList(caseStatus, modeName, taskNumber, taskResult,
                fieldDesignation, handGoods, isAll, idList); //get export list from service
        return exportList;
    }

    /**
     * Knowledge Case pending generate excel file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/pending/xlsx", method = RequestMethod.POST)
    public Object knowledgeCasePendingGenerateExcelFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be exported
        setDictionary(); //set dictionary data
        KnowledgeDealPendingExcelView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPendingExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case pending generate word file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/pending/docx", method = RequestMethod.POST)
    public Object knowledgeCasePendingGenerateWordFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be exported
        setDictionary(); //set dictionary data
        KnowledgeDealPendingWordView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPendingWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case pending generate pdf file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/pending/pdf", method = RequestMethod.POST)
    public Object knowledgeCasePendingGeneratePDFFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be printed
        KnowledgeDealPendingPdfView.setResource(getFontResource()); //set font resource
        setDictionary();  //set dictionary data
        KnowledgeDealPendingPdfView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPendingPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate excel file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/personal/xlsx", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateExcelFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        setDictionary(); //set dictionary data
        KnowledgeDealPersonalExcelView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPersonalExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate word file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/personal/docx", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateWordFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        setDictionary(); //set dictionary data
        KnowledgeDealPersonalWordView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPersonalWordView.buildWordDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Knowledge Case personal generate pdf file request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/generate/personal/pdf", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGenerateFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        List<SerKnowledgeCaseDeal> exportList = getExportList(filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        KnowledgeDealPersonalPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        KnowledgeDealPersonalPdfView.setMessageSource(messageSource);
        InputStream inputStream = KnowledgeDealPersonalPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=knowledge-pending.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
