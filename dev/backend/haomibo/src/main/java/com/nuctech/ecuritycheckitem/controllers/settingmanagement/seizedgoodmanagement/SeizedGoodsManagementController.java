/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2020。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SeizedGoodsManagementController）
 * 文件名：	SeizedGoodsManagementController.java
 * 描述：	Seized Goods management controller.
 * 作者名：	Choe
 * 日期：	2020/01/09
 */

package com.nuctech.ecuritycheckitem.controllers.settingmanagement.seizedgoodmanagement;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.fieldmanagement.FieldManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SerSeizedGood;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.SerSeizedGoodService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Seized Goods management controller.
 */
@RestController
@RequestMapping("/seized-good-management")
public class SeizedGoodsManagementController extends BaseController {

    @Autowired
    SerSeizedGoodService serSeizedGoodService;
    @Autowired
    AuditLogService auditLogService;



    /**
     * Field datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class SeizedGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String goods;
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
     * SerSized Good modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class SeizedModifyRequestBody {

        @NotNull
        Long goodsId;
        @NotNull
        String seizedGoods;
        @NotNull
        String seizedGoodType;
        @NotNull
        String seizedGoodsLevel;

        SerSeizedGood convert2SerSeizedGood() { //create new object from input parameters
            return SerSeizedGood
                    .builder()
                    .goodsId(this.getGoodsId())
                    .seizedGoods(this.getSeizedGoods())
                    .seizedGoodType(this.getSeizedGoodType())
                    .seizedGoodsLevel(this.getSeizedGoodsLevel())
                    .build();
        }
    }

    /**
     * SerSized Good modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class SeizedCreateRequestBody {

        @NotNull
        String seizedGoods;
        @NotNull
        String seizedGoodType;
        @NotNull
        String seizedGoodsLevel;

        SerSeizedGood convert2SerSeizedGood() { //create new object from input parameters
            return SerSeizedGood
                    .builder()
                    .seizedGoods(this.getSeizedGoods())
                    .seizedGoodType(this.getSeizedGoodType())
                    .seizedGoodsLevel(this.getSeizedGoodsLevel())
                    .build();
        }
    }

    /**
     * SerSized Good delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class SeizedDeleteRequestBody {
        @NotNull
        Long goodsId;
    }

    /**
     * SerSized Good create request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_CREATE)
    @RequestMapping(value = "/seized/create", method = RequestMethod.POST)
    public Object goodsCreate(
            @RequestBody @Valid SeizedCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getSeizedGoods(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(serSeizedGoodService.checkGood(requestBody.getSeizedGoods(), null)) {
            auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("UsedSeizedGoods", null, currentLocale), requestBody.getSeizedGoods(),null);
            return new CommonResponseBody(ResponseMessage.USED_SEIZED_GOOD);
        }

        SerSeizedGood serSeizedGood = requestBody.convert2SerSeizedGood();
        serSeizedGoodService.createGood(serSeizedGood);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getSeizedGoods(),null);
        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * SerSized Good modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_MODIFY)
    @RequestMapping(value = "/seized/modify", method = RequestMethod.POST)
    public Object goodsModify(
            @RequestBody @Valid SeizedModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getSeizedGoods(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!serSeizedGoodService.checkSeizedExist(requestBody.getGoodsId())) { // Check if goods id is existing.
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getSeizedGoods(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(serSeizedGoodService.checkGood(requestBody.getSeizedGoods(), requestBody.getGoodsId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("UsedSeizedGoods", null, currentLocale), requestBody.getSeizedGoods(),null);
            return new CommonResponseBody(ResponseMessage.USED_SEIZED_GOOD);
        }


        SerSeizedGood serSeizedGood = requestBody.convert2SerSeizedGood();
        serSeizedGoodService.modifyGood(serSeizedGood);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getSeizedGoods(),null);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * SerSized Good delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
//    @PreAuthorize(Role.Authority.HAS_FIELD_DELETE)
    @RequestMapping(value = "/seized/delete", method = RequestMethod.POST)
    public Object goodsDelete(
            @RequestBody @Valid SeizedDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), requestBody.getGoodsId().toString(),null);
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        serSeizedGoodService.removeGood(requestBody.getGoodsId());
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", requestBody.getGoodsId().toString(),null);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * SerSized Good datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/seized/get-by-filter-and-page", method = RequestMethod.POST)
    public Object goodsGetByFilterAndPage(
            @RequestBody @Valid SeizedGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String goods = "";
        if(requestBody.getFilter() != null) {
            goods = requestBody.getFilter().getGoods();
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

        PageResult<SerSeizedGood> result = serSeizedGoodService.getGoodsListByFilter(sortBy, order, goods, currentPage, perPage); //get list of field from database through fieldService
        long total = result.getTotal(); //get total count
        List<SerSeizedGood> data = result.getDataList();

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






}
