package com.haomibo.haomibo.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ExportType;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.enums.Role;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import com.haomibo.haomibo.models.db.QSysOrg;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.haomibo.haomibo.validation.annotations.ExportTypeValue;
import com.itextpdf.text.Document;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Organization management controller.
 */
@RestController
@RequestMapping("/permission-management/organization-management")
public class OrganizationManagementController extends BaseController {

    /**
     * Organization create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationCreateRequestBody {

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {

            return SysOrg
                    .builder()
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Organization delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationDeleteRequestBody {

        @NotNull
        Long orgId;

    }

    /**
     * Organization datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String orgName;
            String status;
            String parentOrgName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;


    }

    /**
     * Organization modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationModifyRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {
            return SysOrg
                    .builder()
                    .orgId(this.getOrgId())
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();
        }

    }

    /**
     * Organization update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationUpdateStatusRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
        String status;

    }


    /**
     * Organization get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_PARENT = "with_parent";
            static final String WITH_CHILDREN = "with_children";
            static final String WITH_USERS = "with_users";
            static final String WITH_PARENT_AND_USERS = "with_parent_and_users";
            static final String WITH_CHILDREN_AND_USERS = "with_children_and_users";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_PARENT + "|" +
                GetAllType.WITH_CHILDREN + "|" +
                GetAllType.WITH_USERS + "|" +
                GetAllType.WITH_PARENT_AND_USERS + "|" +
                GetAllType.WITH_CHILDREN_AND_USERS)
        String type = GetAllType.BARE;


    }

    /**
     * Organization export request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationExportRequestBody {

        @ExportTypeValue
        String exportType;

    }

    /**
     * Organization create request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_CREATE)
    @RequestMapping(value = "/organization/create", method = RequestMethod.POST)
    public Object organizationCreate(
            @RequestBody @Valid OrganizationCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent org is existing.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Organization modify request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_MODIFY)
    @RequestMapping(value = "/organization/modify", method = RequestMethod.POST)
    public Object organizationModify(
            @RequestBody @Valid OrganizationModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is existing.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent org is existing.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Organization delete request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_DELETE)
    @RequestMapping(value = "/organization/delete", method = RequestMethod.POST)
    public Object organizationDelete(
            @RequestBody @Valid OrganizationDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org has children.
        boolean isHavingChildren = sysOrgRepository.exists(QSysOrg.sysOrg.parentOrgId.eq(requestBody.getOrgId()));
        if (isHavingChildren) {
            // Can't delete if org has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        // Check if org has users.
        boolean isHavingUsers = sysUserRepository.exists(QSysUser.sysUser.orgId.eq(requestBody.getOrgId()));
        if (isHavingUsers) {
            // Can't delete if org has users.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }

        sysOrgRepository.delete(SysOrg.builder().orgId(requestBody.getOrgId()).build());

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Organization update status request.
     */
    @RequestMapping(value = "/organization/update-status", method = RequestMethod.POST)
    public Object organizationUpdateStatus(
            @RequestBody @Valid OrganizationUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is existing.
        Optional<SysOrg> optionalSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()));
        if (!optionalSysOrg.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysOrg sysOrg = optionalSysOrg.get();

        // Update status.
        sysOrg.setStatus(requestBody.getStatus());

        sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Organization get all request.
     * BARE, WITH_PARENT, WITH_CHILDREN, WITH_USERS, WITH_PARENT_AND_USERS, WITH_CHILDREN_AND_USERS.
     */
    @RequestMapping(value = "/organization/get-all", method = RequestMethod.POST)
    public Object organizationGetAll(@RequestBody @Valid OrganizationGetAllRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysOrg> sysOrgList = sysOrgRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysOrgList));

        String type = requestBody.getType();

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        // Set filters for different type.
        switch (type) {

            case OrganizationGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users", "children"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            default:

                break;
        }

        value.setFilters(filters);

        return value;
    }


    /**
     * Organization datatable data.
     */
    @RequestMapping(value = "/organization/get-by-filter-and-page", method = RequestMethod.POST)
    public Object organizationGetByFilterAndPage(
            @RequestBody @Valid OrganizationGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSysOrg builder = QSysOrg.sysOrg;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        OrganizationGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getOrgName())) {
                predicate.and(builder.orgName.contains(filter.getOrgName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getParentOrgName())) {
                predicate.and(builder.parent.orgName.contains(filter.getParentOrgName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysOrgRepository.count(predicate);
        List<SysOrg> data = sysOrgRepository.findAll(predicate, pageRequest).getContent();


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
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ORG,
                        SimpleBeanPropertyFilter.serializeAllExcept("children", "users"));

        value.setFilters(filters);

        return value;
    }

    private InputStream organizationGenerateXlsx4Export() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Persons");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Name");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Age");
            headerCell.setCellStyle(headerStyle);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            Row row = sheet.createRow(2);
            Cell cell = row.createCell(0);
            cell.setCellValue("John Smith");
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(20);
            cell.setCellStyle(style);


            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }


    private InputStream organizationGenerateDocx4Export() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
            mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
            mainDocumentPart.addParagraphOfText("Hello world");


            ObjectFactory factory = Context.getWmlObjectFactory();
            P p = factory.createP();
            R r = factory.createR();
            Text t = factory.createText();
            t.setValue("Hello world");
            r.getContent().add(t);
            p.getContent().add(r);

            int writableWidthTwips = wordPackage.getDocumentModel()
                    .getSections().get(0).getPageDimensions().getWritableWidthTwips();
            int columnNumber = 3;
            Tbl tbl = TblFactory.createTable(3, 3, writableWidthTwips / columnNumber);
            List<Object> rows = tbl.getContent();
            for (Object row : rows) {
                Tr tr = (Tr) row;
                List<Object> cells = tr.getContent();
                for (Object cell : cells) {
                    Tc td = (Tc) cell;
                    td.getContent().add(p);
                }
            }

            mainDocumentPart.getContent().add(tbl);


            wordPackage.save(out);

        } catch (Docx4JException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());

    }

    private InputStream organizationGeneratePdf4Export() {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            PdfPTable table = new PdfPTable(3);
            Stream.of("column header 1", "column header 2", "column header 3")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            table.addCell("row 1, col 1");
            table.addCell("row 1, col 2");
            table.addCell("row 1, col 3");


            PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
            horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(horizontalAlignCell);

            PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
            verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(verticalAlignCell);

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());

    }

    /**
     * Organization export.
     */
    @RequestMapping(value = "/organization/export", method = RequestMethod.POST)
    public Object organizationExport(
            @RequestBody @Valid OrganizationExportRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (ExportType.PDF.getValue().equals(requestBody.getExportType())) {
            InputStream inputStream = organizationGeneratePdf4Export();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=test.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(inputStream));


        }

        if (ExportType.DOCX.getValue().equals(requestBody.getExportType())) {
            InputStream inputStream = organizationGenerateDocx4Export();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=test.docx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/msword"))
                    .body(new InputStreamResource(inputStream));


        }

        if (ExportType.XLSX.getValue().equals(requestBody.getExportType())) {
            InputStream inputStream = organizationGenerateXlsx4Export();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=test.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/x-msexcel"))
                    .body(new InputStreamResource(inputStream));


        }

        return null;

    }

}
