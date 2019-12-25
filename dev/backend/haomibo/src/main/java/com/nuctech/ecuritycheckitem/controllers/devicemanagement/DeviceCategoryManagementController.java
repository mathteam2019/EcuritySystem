/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryManagementController）
 * 文件名：	DeviceCategoryManagementController.java
 * 描述：	DeviceCategory Management Controller
 * 作者名：	Choe
 * 日期：	2019/11/18
 *
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplateWordView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryPdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceCategoryService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/device-classify")
public class DeviceCategoryManagementController extends BaseController {

    @Autowired
    DeviceCategoryService deviceCategoryService;

    /**
     * Device category create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryCreateRequestBody {

        @NotNull
        String categoryNumber;
        @NotNull
        String categoryName;
        @NotNull
        Long parentCategoryId;
        String note;

        SysDeviceCategory convert2SysDeviceCategory() { //create new object from input parameters
            return SysDeviceCategory
                    .builder()
                    .categoryName(this.getCategoryName())
                    .categoryNumber(this.getCategoryNumber())
                    .parentCategoryId(this.getParentCategoryId())
                    .status(SysDeviceCategory.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }
    }

    /**
     * Device Cateogry delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCateogryDeleteRequestBody {

        @NotNull
        Long categoryId;
    }

    /**
     * Device Category datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String categoryName;
            String status;
            String parentCategoryName;
        }

        @NotNull
        @Min(1)
        int currentPage;
        @NotNull
        int perPage;
        Filter filter;
    }

    /**
     * Device Category  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryGenerateRequestBody {

        String idList;  //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data.
        DeviceCategoryGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Device Category modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryModifyRequestBody {

        @NotNull
        Long categoryId;
        @NotNull
        String categoryNumber;
        @NotNull
        String categoryName;
        @NotNull
        Long parentCategoryId;
        String note;

        SysDeviceCategory convert2SysDeviceCategory() { //create new object from input parameters
            return SysDeviceCategory
                    .builder()
                    .categoryId(this.getCategoryId())
                    .categoryName(this.getCategoryName())
                    .categoryNumber(this.getCategoryNumber())
                    .parentCategoryId(this.getParentCategoryId())
                    .status(SysDeviceCategory.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }
    }

    /**
     * Device Category update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryUpdateStatusRequestBody {

        @NotNull
        Long categoryId;

        @NotNull
        @Pattern(regexp = SysDeviceCategory.Status.ACTIVE + "|" + SysDeviceCategory.Status.INACTIVE)
        String status;

    }

    /**
     * Device Category create request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_CREATE)
    @RequestMapping(value = "/category/create", method = RequestMethod.POST)
    public Object deviceCategoryCreate(
            @RequestBody @Valid DeviceCategoryCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent category is existing.
        if (requestBody.getParentCategoryId() != 0 && !deviceCategoryService.checkCategoryExist(requestBody.getParentCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (deviceCategoryService.checkCategoryNameExist(requestBody.getCategoryName(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_CATEGORY_NAME);
        }

        if (deviceCategoryService.checkCategoryNumberExist(requestBody.getCategoryNumber(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_CATEGORY_NUMBER);
        }

        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory();
        deviceCategoryService.createSysDeviceCategory(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device Category modify request.
     *
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_MODIFY)
    @RequestMapping(value = "/category/modify", method = RequestMethod.POST)
    public Object deviceCategoryModify(
            @RequestBody @Valid DeviceCategoryModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (deviceCategoryService.checkChildernCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        if (!deviceCategoryService.checkCategoryExist(requestBody.getCategoryId())) {// Check if category is existing.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!deviceCategoryService.checkCategoryExist(requestBody.getParentCategoryId())) {  // Check if parent category is existing.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (deviceCategoryService.checkArchiveTemplateExist(requestBody.getCategoryId())) {//Check if archive template contain this category
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVE_TEMPLATE);
        }

        if (deviceCategoryService.checkCategoryNameExist(requestBody.getCategoryName(), requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.USED_CATEGORY_NAME);
        }

        if (deviceCategoryService.checkCategoryNumberExist(requestBody.getCategoryNumber(), requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.USED_CATEGORY_NUMBER);
        }

        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory();
        deviceCategoryService.modifySysDeviceCategory(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device Category delete request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_DELETE)
    @RequestMapping(value = "/category/delete", method = RequestMethod.POST)
    public Object deviceCategoryDelete(
            @RequestBody @Valid DeviceCateogryDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (deviceCategoryService.checkChildernCategoryExist(requestBody.getCategoryId())) { // Check if category has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        if (deviceCategoryService.checkArchiveTemplateExist(requestBody.getCategoryId())) { //Check if archive template contain this category
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVE_TEMPLATE);
        }

        deviceCategoryService.removeSysDeviceCategory(requestBody.getCategoryId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device Category get all request.
     * @return
     */
    @RequestMapping(value = "/category/get-all", method = RequestMethod.POST)
    public Object deviceCategoryGetAll() {

        List<SysDeviceCategory> sysDeviceCategoryList = sysDeviceCategoryRepository.findAll();

        for (int i = 0; i < sysDeviceCategoryList.size(); i++) { //set parent's  parent to null so prevent recursion
            SysDeviceCategory deviceCategory = sysDeviceCategoryList.get(i);
            if (deviceCategory.getParent() != null) {
                deviceCategory.getParent().setParent(null);
            }
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceCategoryList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

    /**
     * Device Category datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/category/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceCategoryGetByFilterAndPage(
            @RequestBody @Valid DeviceCategoryGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if (requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName(); //get category name from input parameter
            status = requestBody.getFilter().getStatus(); //get status from from input parameter
            parentCategoryName = requestBody.getFilter().getParentCategoryName(); //get category name from input parameter
        }
        int currentPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();
        currentPage--;

        PageResult<SysDeviceCategory> result = deviceCategoryService.getDeviceCategoryListByPage(categoryName, status, parentCategoryName,
                currentPage, perPage); //get list of SysDeviceCategory from database through deviceCategoryService
        long total = result.getTotal();
        List<SysDeviceCategory> data = result.getDataList();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(total) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) total) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current Page
                        .to(perPage * currentPage + data.size()) //set end index of current page
                        .data(data)
                        .build()));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters(); //set filters
        value.setFilters(filters);

        return value;
    }

    /**
     * Device Category generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_EXPORT)
    @RequestMapping(value = "/category/xlsx", method = RequestMethod.POST)
    public Object deviceCategoryGenerateExcelFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if (requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();//get category name from input parameter
            status = requestBody.getFilter().getStatus();//get status from from input parameter
            parentCategoryName = requestBody.getFilter().getParentCategoryName();//get category name from input parameter
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of SysDeviceCategory from database through deviceCategoryService
        setDictionary(); //set dictionary data
        DeviceCategoryExcelView.setMessageSource(messageSource);
        InputStream inputStream = DeviceCategoryExcelView.buildExcelDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Category generate file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/category/docx", method = RequestMethod.POST)
    public Object deviceCategoryGenerateWordFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if (requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();//get category name from input parameter
            status = requestBody.getFilter().getStatus();//get status from from input parameter
            parentCategoryName = requestBody.getFilter().getParentCategoryName();//get category name from input parameter
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of SysDeviceCategory from database through deviceCategoryService
        setDictionary(); //set dictionary data
        DeviceCategoryWordView.setMessageSource(messageSource);
        InputStream inputStream = DeviceCategoryWordView.buildWordDocument(exportList); //create inputstream of result to be exported


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.docx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device Category generate pdf file request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_PRINT)
    @RequestMapping(value = "/category/pdf", method = RequestMethod.POST)
    public Object deviceCategoryGeneratePDFFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if (requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();//get category name from input parameter
            status = requestBody.getFilter().getStatus();//get status from from input parameter
            parentCategoryName = requestBody.getFilter().getParentCategoryName();//get category name from input parameter
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList()); //get list of SysDeviceCategory from database through deviceCategoryService
        DeviceCategoryPdfView.setResource(getFontResource()); //set font resource
        setDictionary(); //set dictionary data
        DeviceCategoryPdfView.setMessageSource(messageSource);
        InputStream inputStream = DeviceCategoryPdfView.buildPDFDocument(exportList); //create inputstream of result to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.pdf"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Device Category update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_UPDATE_STATUS)
    @RequestMapping(value = "/category/update-status", method = RequestMethod.POST)
    public Object deviceCategoryUpdateStatus(
            @RequestBody @Valid DeviceCategoryUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!deviceCategoryService.checkCategoryExist(requestBody.getCategoryId())) {        // Check if category is existing.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        if (deviceCategoryService.checkChildernCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }
        if (deviceCategoryService.checkArchiveTemplateExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVE_TEMPLATE);
        }
        deviceCategoryService.updateStatus(requestBody.getCategoryId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }
}
