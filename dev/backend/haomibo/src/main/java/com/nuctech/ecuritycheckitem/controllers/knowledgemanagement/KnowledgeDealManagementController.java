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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.HistoryTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingPdfView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingExcelView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPendingWordView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalPdfView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalExcelView;
import com.nuctech.ecuritycheckitem.export.knowledgemanagement.KnowledgeDealPersonalWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCase;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDealImage;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.DeviceImageModel;
import com.nuctech.ecuritycheckitem.models.reusables.DownImage;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistoryDetail;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;
import com.nuctech.ecuritycheckitem.service.knowledgemanagement.KnowledgeService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeDealManagementController extends BaseController {

    @Autowired
    KnowledgeService knowledgeService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    PlatformCheckService platformCheckService;



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
            Long fieldId;
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
        String sort;
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
     * Knowledge Case Deal delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class KnowledgeCaseDeleteRequestBody {

        @NotNull
        Long caseDealId;

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
        String sort;
        KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter;
        String locale;
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
        String sort;
        List<String> tagList;
    }

    ////@PreAuthorize(Role.Authority.HAS_KNOWLEDGECASE_CREATE)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object insertKnowledgeCase(@RequestBody @Valid KnowledgeCaseInsertRequestBody requestBody, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        HistoryDetail history = historyService.getDetail(requestBody.getHistoryId());
        if (history == null) { //if specified history id doesn't exist
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(knowledgeService.checkKnowledgeExistByTask(history.getTaskId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ExistKnowledge", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //prepare knowledgecase object to insert
        SerKnowledgeCase knowledgeCase = initSerKnowCaseDealFromHistory(history);
        knowledgeCase.setTaskId(history.getTaskId());
        knowledgeCase.setCaseStatus(SerKnowledgeCase.Status.SUBMIT_APPROVAL);

        knowledgeCase.setCaseCollectUserId(requestBody.getUserId());

        //insert new knowledge case and get new id
        Long knowledgeId = knowledgeService.insertNewKnowledgeCase(knowledgeCase, requestBody.getTagList());
        if (knowledgeId == null) { //failed inserting
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.FAILED_INSERT_KNOWLEDGECASE);
        }
        knowledgeCase.setCaseId(knowledgeId);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    private SerKnowledgeCase initSerKnowCaseDealFromHistory(HistoryDetail history) {

        SerKnowledgeCase serKnowledgeCaseDeal = new SerKnowledgeCase();
        serKnowledgeCaseDeal.setTaskId(history.getTaskId());//.setSerTask(history.getSerTask());
        serKnowledgeCaseDeal.setModeId(history.getModeId());//.setSysWorkMode(history.getSysWorkMode());
        serKnowledgeCaseDeal.setScanId(history.getScanId());//.setSerScan(history.getSerScan());
        serKnowledgeCaseDeal.setWorkflowId(history.getWorkflowId());//.setSysWorkflow(history.getSysWorkflow());
        serKnowledgeCaseDeal.setScanDeviceId(history.getScanDeviceId());//.setScanDevice(history.getScanDevice());
        serKnowledgeCaseDeal.setScanImageId(history.getScanImageId());//.setScanImage(history.getScanImage());
        serKnowledgeCaseDeal.setScanAtrResult(history.getScanATRResult());
        serKnowledgeCaseDeal.setScanFootAlarm(history.getScanFootAlarm());
        serKnowledgeCaseDeal.setScanStartTime(history.getScanStartTime());
        serKnowledgeCaseDeal.setScanEndTime(history.getScanEndTime());
        serKnowledgeCaseDeal.setScanPointmanId(history.getScanPointmanId());//.setScanPointsman(history.getScanPointsman());
        serKnowledgeCaseDeal.setScanPointsmanName(history.getScanPointsmanName());
        serKnowledgeCaseDeal.setAssignJudgeId(history.getAssignJudgeId());
        serKnowledgeCaseDeal.setAssignHandId(history.getAssignHandId());
        serKnowledgeCaseDeal.setAssignWorkflowJudgeId(history.getAssignWorkflowJudgeId());
        serKnowledgeCaseDeal.setAssignWorkflowHandId(history.getAssignWorkflowHandId());
        serKnowledgeCaseDeal.setAssignUserJudgeId(history.getAssignUserJudgeId());
        serKnowledgeCaseDeal.setAssignHandId(history.getAssignHandId());
        serKnowledgeCaseDeal.setAssignJudgeUserName(history.getAssignJudgeUserName());
        serKnowledgeCaseDeal.setAssignHandUserName(history.getAssignHandUserName());
        serKnowledgeCaseDeal.setAssignJudgeDeviceId(history.getAssignJudgeDeviceId());//.setAssignJudgeDevice(history.getAssignJudgeDevice());
        serKnowledgeCaseDeal.setAssignHandDeviceId(history.getAssignHandDeviceId());//.setAssignHandDevice(history.getAssignHandDevice());
        serKnowledgeCaseDeal.setAssignJudgeStartTime(history.getAssignJudgeStartTime());
        serKnowledgeCaseDeal.setAssignJudgeEndTime(history.getAssignJudgeEndTime());
        serKnowledgeCaseDeal.setAssignHandStartTime(history.getAssignHandStartTime());
        serKnowledgeCaseDeal.setAssignHandEndTime(history.getAssignHandEndTime());
        serKnowledgeCaseDeal.setAssignJudgeTimeout(history.getAssignJudgeTimeout());
        serKnowledgeCaseDeal.setAssignHandTimeout(history.getAssignHandTimeout());
        serKnowledgeCaseDeal.setAssignJudgeStatus(history.getAssignJudgeStatus());
        serKnowledgeCaseDeal.setAssignHandStatus(history.getAssignHandStatus());


        serKnowledgeCaseDeal.setJudgeGraphId(history.getJudgeGraphId());//.setJudgeGraph(history.getJudgeGraph());
        serKnowledgeCaseDeal.setJudgeWorkflowId(history.getJudgeWorkflowId());//.setJudgeWorkflow(history.getJudgeWorkflow());
        serKnowledgeCaseDeal.setJudgeDeviceId(history.getJudgeDeviceId());//.setJudgeDevice(history.getJudgeDevice());
        serKnowledgeCaseDeal.setJudgeResult(history.getJudgeResult());
        serKnowledgeCaseDeal.setJudgeTimeout(history.getJudgeTimeout());
        serKnowledgeCaseDeal.setHandExaminationId(history.getHandExaminationId());//.setHandExamination(history.getHandExamination());
        serKnowledgeCaseDeal.setHandWorkflowId(history.getHandWorkflowId());//.setHandWorkflow(history.getHandWorkflow());
        serKnowledgeCaseDeal.setHandDeviceId(history.getHandDeviceId());//.setHandDevice(history.getHandDevice());
        serKnowledgeCaseDeal.setHandResult(history.getHandResult());
        serKnowledgeCaseDeal.setHandStartTime(history.getHandStartTime());
        serKnowledgeCaseDeal.setHandEndTime(history.getHandEndTime());
        serKnowledgeCaseDeal.setHandUserId(history.getHandUserId());//.setHandUser(history.getHandUser());
        serKnowledgeCaseDeal.setHandTaskResult(history.getHandTaskResult());
        serKnowledgeCaseDeal.setHandGoods(history.getHandGoods());
        serKnowledgeCaseDeal.setHandGoodsGrade(history.getHandGoodsGrade());
        serKnowledgeCaseDeal.setHandCollectSign(history.getHandCollectSign());
        //serKnowledgeCaseDeal.setHandAttachedId(history.getHandAttached());
        serKnowledgeCaseDeal.setHandCollectLabel(history.getHandCollectLabel());
        serKnowledgeCaseDeal.setHandAppraise(history.getHandAppraise());
        serKnowledgeCaseDeal.setHandAppraiseSecond(history.getHandAppraiseSecond());
        serKnowledgeCaseDeal.setJudgeStartTime(history.getJudgeStartTime());
        serKnowledgeCaseDeal.setJudgeEndTime(history.getJudgeEndTime());
        serKnowledgeCaseDeal.setJudgeUserId(history.getJudgeUserId());//.setJudgeUser(history.getJudgeUser());
        serKnowledgeCaseDeal.setJudgeAssignTimeout(history.getJudgeAssignTimeout());
        serKnowledgeCaseDeal.setJudgeStatus(history.getJudgeStatus());

        serKnowledgeCaseDeal.setTaskStatus(history.getTaskStatus());
        serKnowledgeCaseDeal.setModeName(history.getModeName());
        serKnowledgeCaseDeal.setFieldId(history.getFieldId());

        serKnowledgeCaseDeal.setFieldDesignation(history.getFieldDesignation());

        serKnowledgeCaseDeal.setTaskNumber(history.getTaskNumber());
        serKnowledgeCaseDeal.setScanDeviceName(history.getScanDeviceName());
        serKnowledgeCaseDeal.setJudgeDeviceName(history.getJudgeDeviceName());
        serKnowledgeCaseDeal.setHandDeviceName(history.getHandDeviceName());
        serKnowledgeCaseDeal.setAssignJudgeDeviceName(history.getAssignJudgeDeviceName());
        serKnowledgeCaseDeal.setAssignHandDeviceName(history.getAssignHandDeviceName());
        serKnowledgeCaseDeal.setJudgeUserName(history.getJudgeUserName());
        serKnowledgeCaseDeal.setHandUserName(history.getHandUserName());
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
        Date startTime = new Date();

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
        Long fieldId = null;
        String handGoods = "";
        if (filter != null) {
            caseStatus = filter.getCaseStatus(); //get case status from input parameter
            modeName = filter.getModeName(); //get mode name from input parameter
            taskNumber = filter.getTaskNumber(); //get task number from input parameter
            taskResult = filter.getTaskResult(); //get task result from input parameter
            fieldId = filter.getFieldId(); //get field name from input parameter
            handGoods = filter.getHandGoods(); //get handgoods from input parameter
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
        PageResult<SerKnowledgeCase> result = knowledgeService.getDealListByFilter(sortBy, order, caseStatus, taskNumber, modeName, taskResult,
                fieldId, handGoods, currentPage, perPage); //get result from database through service
        long total = result.getTotal();
        List<SerKnowledgeCase> data = result.getDataList();
        setDictionary(Constants.CHINESE_LOCALE);
        for(int index = 0; index < data.size(); index ++) {
            String originalHandGoods = data.get(index).getHandGoods();
            List<String> handGoodsList = new ArrayList<>();

            if(!StringUtils.isEmpty(originalHandGoods)) {

                String[] splits = originalHandGoods.split(",");
                for(int i = 0; i < splits.length; i ++) {
                    String convertHandGoods = ConstantDictionary.getDataValue(splits[i], String.valueOf(Constants.SEIZED_DICTIONARY_ID));
                    handGoodsList.add(convertHandGoods);
                }
            }
            data.get(index).setHandGoodsList(handGoodsList);
        }
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
                .getDefaultFilters();
        value.setFilters(filters);

        Date endTime = new Date();
        long idff = endTime.getTime() - startTime.getTime();

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
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");

            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!knowledgeService.checkKnowledgeExist(requestBody.getCaseId())) { // Check if knowledge case is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        knowledgeService.updateStatus(requestBody.getCaseId(), requestBody.getStatus()); //update db through knowledgeService
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Knowledge Case delete request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object knowledgeCaseDelete(
            @RequestBody @Valid KnowledgeCaseDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("KnowledgeCase", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        knowledgeService.delete(requestBody.getCaseDealId()); //update db through knowledgeService
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
    private List<SerKnowledgeCase> getExportList(String sortBy, String order, KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter, boolean isAll, String idList) {
        String caseStatus = "";
        String modeName = "";
        String taskNumber = "";
        String taskResult = "";
        Long fieldId = null;
        String handGoods = "";
        if (filter != null) {
            caseStatus = filter.getCaseStatus(); //get case status from input parameter
            modeName = filter.getModeName(); //get mode name from input parameter
            taskNumber = filter.getTaskNumber(); //get task number from input parameter
            taskResult = filter.getTaskResult(); //get task result from input parameter
            fieldId = filter.getFieldId(); //get field name from input parameter
            handGoods = filter.getHandGoods(); //get handgoods from input parameter
        }
        List<SerKnowledgeCase> exportList = knowledgeService.getDealExportList(sortBy, order, caseStatus, modeName, taskNumber, taskResult,
                fieldId, handGoods, isAll, idList); //get export list from service
        return exportList;
    }

    private MappingJacksonValue getImageDownload( KnowledgeCaseGenerateRequestBody requestBody) {
        List<SerPlatformCheckParams> paramsList = platformCheckService.findAll();
        boolean isEnabledCartoon = false;
        boolean isEnabledOriginal = false;
        if(paramsList.size() > 0) {
            String historyDataExport = paramsList.get(0).getHistoryDataExport();
            String splitList[] = historyDataExport.split(",");
            for(int i = 0; i < splitList.length; i ++) {
                if(splitList[i].equals(SerPlatformCheckParams.HistoryData.CARTOON)) {
                    isEnabledCartoon = true;
                }
                if(splitList[i].equals(SerPlatformCheckParams.HistoryData.ORIGINAL)) {
                    isEnabledOriginal = true;
                }
            }
        }
        DownImage downImage = new DownImage();
        downImage.setEnabledCartoon(isEnabledCartoon);
        downImage.setEnabledOriginal(isEnabledOriginal);
        if(isEnabledCartoon == true || isEnabledOriginal == true) {
            Map<String, String> sortParams = new HashMap<String, String>();
            String sortBy = "";
            String order = "";
            KnowLedgeDealGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
            if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
                sortParams = Utils.getSortParams(requestBody.getSort());
                if (!sortParams.isEmpty()) {
                    sortBy = sortParams.get("sortBy");
                    order = sortParams.get("order");
                }
            }
            String caseStatus = "";
            String modeName = "";
            String taskNumber = "";
            String taskResult = "";
            Long fieldId = null;
            String handGoods = "";
            if (filter != null) {
                caseStatus = filter.getCaseStatus(); //get case status from input parameter
                modeName = filter.getModeName(); //get mode name from input parameter
                taskNumber = filter.getTaskNumber(); //get task number from input parameter
                taskResult = filter.getTaskResult(); //get task result from input parameter
                fieldId = filter.getFieldId(); //get field name from input parameter
                handGoods = filter.getHandGoods(); //get handgoods from input parameter
            }
            List<SerKnowledgeCaseDealImage> exportList = knowledgeService.getDealImageList(sortBy, order, caseStatus, modeName, taskNumber, taskResult,
                    fieldId, handGoods, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service

            List<String> cartoonImageList = new ArrayList<>();
            List<String> originalImageList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for(int i = 0; i < exportList.size(); i ++) {
                String scanDeviceImages = exportList.get(i).getSerScan().getScanDeviceImages();
                try {
                    List<DeviceImageModel> deviceImageModel = objectMapper.readValue(scanDeviceImages, objectMapper.getTypeFactory().constructCollectionType(
                            List.class, DeviceImageModel.class));
                    for(int j = 0; j < deviceImageModel.size(); j ++) {
                        if(isEnabledCartoon) {
                            cartoonImageList.add(deviceImageModel.get(j).getCartoon());
                        }
                        if(isEnabledOriginal) {
                            originalImageList.add(deviceImageModel.get(j).getImage());
                        }
                    }
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                if(isEnabledCartoon) {

                }
            }
            downImage.setCartoonImageList(cartoonImageList);
            downImage.setOriginalImageList(originalImageList);
        }
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, downImage));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);
        return value;
    }

    /**
     * Task table original image file request.
     */
    @RequestMapping(value = "/generate/image", method = RequestMethod.POST)
    public Object knowledgeCasePersonalGetOriginalImageFile(@RequestBody @Valid KnowledgeCaseGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        return getImageDownload(requestBody);

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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be exported
        setDictionary(requestBody.getLocale()); //set dictionary data
        KnowledgeDealPendingExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPendingExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPendingExcelView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be exported
        setDictionary(requestBody.getLocale()); //set dictionary data
        KnowledgeDealPendingWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPendingWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPendingWordView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list to be printed
        KnowledgeDealPendingPdfView.setResource(getFontResource()); //set font resource
        setDictionary(requestBody.getLocale());  //set dictionary data
        KnowledgeDealPendingPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPendingPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPendingPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        //KnowledgeDealPendingPdfView.setResourceFile(resourceFile);
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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        setDictionary(requestBody.getLocale()); //set dictionary data
        KnowledgeDealPersonalExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPersonalExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPersonalExcelView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        setDictionary(requestBody.getLocale()); //set dictionary data
        KnowledgeDealPersonalWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPersonalWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPersonalWordView.setCurrentLocale(Locale.ENGLISH);
        }
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
        List<SerKnowledgeCase> exportList = getExportList(sortBy, order, filter, requestBody.getIsAll(), requestBody.getIdList()); //get export list from service
        KnowledgeDealPersonalPdfView.setResource(getFontResource()); //set font resource
        setDictionary(requestBody.getLocale()); //set dictionary data
        KnowledgeDealPersonalPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            KnowledgeDealPersonalPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            KnowledgeDealPersonalPdfView.setCurrentLocale(Locale.ENGLISH);
        }
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
