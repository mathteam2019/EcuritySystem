/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName ArchiveManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.mysql.cj.xdevapi.JsonArray;
import com.mysql.cj.xdevapi.JsonParser;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchivePdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device-management/document-management")
public class ArchiveManagementController extends BaseController {
    /**
     * Archive datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String archivesName;
            String status;
            String categoryName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Archive update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveUpdateStatusRequestBody {

        @NotNull
        Long archiveId;

        @NotNull
        @Pattern(regexp = SerArchive.Status.ACTIVE + "|" + SerArchive.Status.INACTIVE)
        String status;

    }


    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveCreateRequestBody {

        @NotNull
        Long archivesTemplateId;

        @NotNull
        String archivesName;

        @NotNull
        String archivesNumber;

//        @NotNull
//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String note;

        private MultipartFile imageUrl;

        String json;



        SerArchive convert2SerArchive() {

            return SerArchive
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
//                    .categoryId(this.getCategoryId())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SerArchive.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveModifyRequestBody {

        @NotNull
        Long archiveId;

        @NotNull
        Long archivesTemplateId;

        @NotNull
        String archivesName;

        @NotNull
        String archivesNumber;

//        @NotNull
//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String note;

        private MultipartFile imageUrl;

        String json;

        SerArchive convert2SerArchive(Long createdBy, Date createdTime) {

            return SerArchive
                    .builder()
                    .archiveId(this.getArchiveId())
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
//                    .categoryId(this.getCategoryId())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SerArchive.Status.INACTIVE)
                    .createdBy(createdBy)
                    .createdTime(createdTime)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Archive delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveDeleteRequestBody {

        @NotNull
        Long archiveId;
    }


    /**
     * Archive  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGenerateRequestBody {
        @NotNull
        String exportType;

        String idList;
        @NotNull
        Boolean isAll;

        ArchiveGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Archive datatable data.
     */
    @RequestMapping(value = "/archive/get-by-filter-and-page", method = RequestMethod.POST)
    public Object archiveGetByFilterAndPage(
            @RequestBody @Valid ArchiveGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSerArchive builder = QSerArchive.serArchive;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        ArchiveGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getArchivesName())) {
                predicate.and(builder.archivesName.contains(filter.getArchivesName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.archiveTemplate.deviceCategory.categoryName.contains(filter.getCategoryName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serArchiveRepository.count(predicate);
        List<SerArchive> data = serArchiveRepository.findAll(predicate, pageRequest).getContent();


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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));;

        value.setFilters(filters);

        return value;
    }

    /**
     * Archive update status request.
     */
    @RequestMapping(value = "/archive/update-status", method = RequestMethod.POST)
    public Object archiveUpdateStatus(
            @RequestBody @Valid ArchiveUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is existing.
        Optional<SerArchive> optionalSerArchive = serArchiveRepository.findOne(QSerArchive.
                serArchive.archiveId.eq(requestBody.getArchiveId()));
        if (!optionalSerArchive.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchive serArchive = optionalSerArchive.get();

        // Update status.
        serArchive.setStatus(requestBody.getStatus());

        // Add edited info.
        serArchive.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive create request.
     */
    @RequestMapping(value = "/archive/create", method = RequestMethod.POST)
    public Object archiveCreate(
            @ModelAttribute @Valid ArchiveCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        // Check if template is valid
        if (!serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(requestBody.getArchivesTemplateId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchive serArchive = requestBody.convert2SerArchive();

        // Process portrait file.
        MultipartFile portraitFile = requestBody.getImageUrl();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Save file name.
                    serArchive.setImageUrl(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // Add created info.
        serArchive.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveRepository.save(serArchive);

        //Object[] arrayReceipients = JSONArray.toArray (JSONArray.fromObject(requestBody.getJsonArchiveValueList()));
        ObjectMapper mapper = new ObjectMapper();
        try {
            SerArchive jsonArchive = mapper.readValue(requestBody.getJson(), SerArchive.class);
            List<SerArchiveValue> archiveValueList = jsonArchive.getArchiveValueList();
            if(archiveValueList != null) {
                for(int i = 0; i < archiveValueList.size(); i ++) {
                    SerArchiveValue archiveValue = archiveValueList.get(i);
                    archiveValue.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                    archiveValue.setArchiveId(serArchive.getArchiveId());
                    serArchiveValueRepository.save(archiveValue);
                }
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }


        //add indicators value



        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Archive modify request.
     */
    @RequestMapping(value = "/archive/modify", method = RequestMethod.POST)
    public Object archiveModify(
            @ModelAttribute @Valid ArchiveModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        // Check if template is valid
        if (!serArchiveTemplateRepository.exists(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(requestBody.getArchivesTemplateId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchive oldSerArchive = serArchiveRepository.findOne(QSerArchive.serArchive
                .archiveId.eq(requestBody.getArchiveId())).orElse(null);

        if(sysDeviceRepository.findOne(QSysDevice.
                sysDevice.archiveId.eq(oldSerArchive.getArchiveId())).isPresent()) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }

        //check if archive exist
        if(oldSerArchive == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchive serArchive = requestBody.convert2SerArchive(oldSerArchive.getCreatedBy(), oldSerArchive.getCreatedTime());

        // Process portrait file.
        MultipartFile portraitFile = requestBody.getImageUrl();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Save file name.
                    serArchive.setImageUrl(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Add edit info.
        serArchive.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());



        //remove original indicators value
        if(oldSerArchive.getArchiveValueList() != null) {
            for(int i = 0; i < oldSerArchive.getArchiveValueList().size(); i ++) {
                SerArchiveValue archiveValue = oldSerArchive.getArchiveValueList().get(i);
                serArchiveValueRepository.delete(archiveValue);
            }
        }

        //add new indicators value
        ObjectMapper mapper = new ObjectMapper();
        try {
            SerArchive jsonArchive = mapper.readValue(requestBody.getJson(), SerArchive.class);
            List<SerArchiveValue> archiveValueList = jsonArchive.getArchiveValueList();
            for(int i = 0; i < archiveValueList.size(); i ++) {
                SerArchiveValue archiveValue = archiveValueList.get(i);
                archiveValue.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                archiveValue.setArchiveId(serArchive.getArchiveId());
                serArchiveValueRepository.save(archiveValue);
            }
        } catch(Exception ex) {

        }


        serArchiveRepository.save(serArchive);


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive delete request.
     */
    @RequestMapping(value = "/archive/delete", method = RequestMethod.POST)
    public Object archiveDelete(
            @RequestBody @Valid ArchiveDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchive serArchive = serArchiveRepository.findOne(QSerArchive.serArchive
                .archiveId.eq(requestBody.getArchiveId())).orElse(null);

        //check archive exist or not
        if(serArchive == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(sysDeviceRepository.findOne(QSysDevice.
                sysDevice.archiveId.eq(serArchive.getArchiveId())).isPresent()) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }


        //remove it's indicators value
        if(serArchive.getArchiveValueList() != null) {
            for(int i = 0; i < serArchive.getArchiveValueList().size(); i ++) {
                serArchiveValueRepository.delete(serArchive.getArchiveValueList().get(i));
            }
        }

        serArchiveRepository.delete(serArchive);


        return new CommonResponseBody(ResponseMessage.OK);
    }



    /**
     * Archive  get all request.
     */
    @RequestMapping(value = "/archive/get-all", method = RequestMethod.POST)
    public Object archiveGetAll() {


        List<SerArchive> serArchiveList = serArchiveRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Archive generate file request.
     */
    @RequestMapping(value = "/archive/export", method = RequestMethod.POST)
    public Object archiveGenerateFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSerArchive builder = QSerArchive.serArchive;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        ArchiveGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getArchivesName())) {
                predicate.and(builder.archivesName.contains(filter.getArchivesName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.archiveTemplate.deviceCategory.categoryName.contains(filter.getCategoryName()));
            }
        }

        //get all archive list
        List<SerArchive> archiveList = StreamSupport
                .stream(serArchiveRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SerArchive> exportList = new ArrayList<>();
        if(requestBody.getIsAll() == false) {
            String[] splits = requestBody.getIdList().split(",");
            for(int i = 0; i < archiveList.size(); i ++) {
                SerArchive archive = archiveList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(archive.getArchiveId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(archive);
                }
            }
        } else {
            exportList = archiveList;
        }

        if(requestBody.exportType.equals("excel")) {
            InputStream inputStream = DeviceArchiveExcelView.buildExcelDocument(exportList);



            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=archive.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/x-msexcel"))
                    .body(new InputStreamResource(inputStream));
        }
        InputStream inputStream = DeviceArchivePdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
