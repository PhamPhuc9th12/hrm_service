package org.example.hrm_salary.core.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.hrm_salary.app.api.SalaryTemplateApi;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnBasicRequest;
import org.example.hrm_salary.app.dto.request.SalaryTemplateRequest.SalaryTemplateRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnBasicResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.GroupSalaryTemplateResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateDetailResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.TemplateSalaryItems;
import org.example.hrm_salary.app.dto.response.department.DataDepartmentResponse;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.constant.IdAndName;
import org.example.hrm_salary.core.domain.constant.IdResponse;
import org.example.hrm_salary.core.domain.entity.*;
import org.example.hrm_salary.core.domain.enums.ApplicablesType;
import org.example.hrm_salary.core.domain.specification.SpecificationSalaryTemplate;
import org.example.hrm_salary.core.port.mapper.GroupSalaryColumnMapper;
import org.example.hrm_salary.core.port.mapper.SalaryColumnsMapper;
import org.example.hrm_salary.core.port.mapper.SalaryTemplateMapper;
import org.example.hrm_salary.core.port.proxy.ServiceProxy;
import org.example.hrm_salary.core.port.repository.*;
import org.example.hrm_salary.app.dto.response.employee.DataEmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SalaryTemplateService implements SalaryTemplateApi {

    private final CustomRepository customRepository;
    private final SalaryTemplateRepository salaryTemplateRepository;
    private final SalaryTemplateMapper salaryTemplateMapper;
    private final SalaryTemplateApplicableRepository salaryTemplateApplicableRepository;
    private GroupSalaryColumnRepository groupSalaryColumnRepository;
    private GroupSalaryColumnMapper groupSalaryColumnMapper;
    private SalaryColumnsMapper salaryColumnsMapper;
    private SalaryColumnsRepository salaryColumnsRepository;
    private final SalaryTemplatesSalaryColumnsRepository salaryTemplatesSalaryColumnsRepository;
    private final ServiceProxy serviceProxyImpl;

    @Override
    public Page<SalaryTemplateResponse> getSalaryTemplatePage(Long id, String search, String name, Pageable pageable) {
        Page<SalaryTemplatesEntity> salaryTemplatesEntities = getSalaryTemplateEntitySearch(id, search, name, pageable);
        if (salaryTemplatesEntities.isEmpty()) throw new RuntimeException(ErrorCode.LIST_IS_EMPTY);
        List<Long> templateIds = salaryTemplatesEntities.stream().map(SalaryTemplatesEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> templateIdNumberOfApplicableMap = salaryTemplateApplicableRepository
                .findAllBySalaryTemplatesIdIn(templateIds)
                .stream().collect(Collectors.groupingBy(
                        SalaryTemplateApplicablesEntity::getSalaryTemplatesId,
                        Collectors.mapping(SalaryTemplateApplicablesEntity::getId, Collectors.counting())
                ));

        return salaryTemplatesEntities.map(
                salaryTemplatesEntity -> {
                    SalaryTemplateResponse salaryTemplateResponse = salaryTemplateMapper
                            .getResponseFromEntity(salaryTemplatesEntity);
                    salaryTemplateResponse.setNumberOfApplicable(templateIdNumberOfApplicableMap
                            .get(salaryTemplatesEntity.getId()));
                    return salaryTemplateResponse;
                }
        );
    }
    @Override
    public SalaryTemplateDetailResponse getSalaryTemplateById(Long templateId) {
        SalaryTemplatesEntity salaryTemplatesEntity = customRepository.getSalaryTemplateEntityById(templateId);
        SalaryTemplateDetailResponse salaryTemplateResponse = salaryTemplateMapper
                .getResponseDetailFromEntity(salaryTemplatesEntity);
        List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities =
                salaryTemplatesSalaryColumnsRepository.findAllBySalaryTemplatesId(templateId);
        return getSalaryTemplate(templateId, salaryTemplatesSalaryColumnsEntities, salaryTemplateResponse);
    }

    @Override
    @Transactional
    public IdResponse createSalaryTemplate(SalaryTemplateRequest salaryTemplateRequest) {
        checkExitRecord(salaryTemplateRequest);
        SalaryTemplatesEntity salaryTemplatesEntity = salaryTemplateMapper.getEntityFromRequest(salaryTemplateRequest);
        salaryTemplateRepository.save(salaryTemplatesEntity);
        // create salary
        createTemplateLines(salaryTemplateRequest, salaryTemplatesEntity);
        createTeamPlateApplicableType(salaryTemplateRequest, salaryTemplatesEntity);
        return IdResponse.builder()
                .id(salaryTemplatesEntity.getId())
                .build();
    }

    @Override
    @Transactional
    public void deleteSalaryTemplate(Long salaryTemplateId) {
        SalaryTemplatesEntity salaryTemplatesEntity = customRepository.getSalaryTemplateEntityById(salaryTemplateId);
        salaryTemplateRepository.delete(salaryTemplatesEntity);
    }

    @Override
    @Transactional
    public IdResponse updateSalaryTemplate(Long salaryTemplateId, SalaryTemplateRequest salaryTemplateRequest) {
        checkExitRecord(salaryTemplateRequest);
        SalaryTemplatesEntity salaryTemplatesEntity = customRepository.getSalaryTemplateEntityById(salaryTemplateId);
        salaryTemplateMapper.updateSalaryTemplate(salaryTemplatesEntity, salaryTemplateRequest);
        salaryTemplateRepository.save(salaryTemplatesEntity);
        createTemplateLines(salaryTemplateRequest, salaryTemplatesEntity);
        createTeamPlateApplicableType(salaryTemplateRequest, salaryTemplatesEntity);

        return IdResponse.builder()
                .id(salaryTemplatesEntity.getId())
                .build();
    }
    @Override
    public void exportSalaryTemplateExcel(Long templateId)  {
        List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities =
                salaryTemplatesSalaryColumnsRepository.findAllBySalaryTemplatesId(templateId);
        Map<String, List<Long>> indexTemplateLineMap = getStartEndIndex(salaryTemplatesSalaryColumnsEntities);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Row headerRow = sheet.createRow(0);
        //create header default for employee
        createHeaderDefault(sheet, headerRow, headerStyle);
        //create header for TemplateItemLines
        createHeaderTemplateLines(templateId, indexTemplateLineMap, sheet, headerRow, headerStyle);
        //create Data for TemplateItemLines
        setDataExcelEmployee(templateId, dataStyle, sheet);
        try (FileOutputStream out = new FileOutputStream("D:\\SalaryTemplateReport.xlsx")) {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDataExcelEmployee(Long templateId, CellStyle dataStyle, Sheet sheet) {
        List<Long> columnIds = salaryTemplatesSalaryColumnsRepository
                .findAllBySalaryTemplatesId(templateId).stream()
                .map(SalaryTemplatesSalaryColumnsEntity::getSalaryColumnsId).collect(Collectors.toList());
        List<DataEmployeeResponse> dataEmployeeResponses = getEmployeesExport(templateId);
        for (int i = 0; i < dataEmployeeResponses.size(); i++) {
            Row dataRow = sheet.createRow(i + 2);
            Cell sttCell = dataRow.createCell(0);
            sttCell.setCellValue(i + 3);
            sttCell.setCellStyle(dataStyle);
            Cell codeCell = dataRow.createCell(1);
            codeCell.setCellValue(dataEmployeeResponses.get(i).getCode());
            codeCell.setCellStyle(dataStyle);
            Cell nameCell = dataRow.createCell(2);
            nameCell.setCellValue(dataEmployeeResponses.get(i).getFirstName() + " " + dataEmployeeResponses.get(i).getLastName());
            nameCell.setCellStyle(dataStyle);
            Cell positionCell = dataRow.createCell(3);
            positionCell.setCellValue(dataEmployeeResponses.get(i).getPosition().getName());
            positionCell.setCellStyle(dataStyle);
            Cell salaryBasicCell = dataRow.createCell(4);
            salaryBasicCell.setCellValue(5000000);
            salaryBasicCell.setCellStyle(dataStyle);
            // set Data column in group
            for (int j = 0; j < columnIds.size(); j++) {
                Cell itemCell = dataRow.createCell(j + 5);
                itemCell.setCellValue(200000);
                itemCell.setCellStyle(dataStyle);
            }
        }
    }

    private void createHeaderDefault(Sheet sheet, Row headerRow, CellStyle headerStyle) {
        for (int i = 0; i < 5; i++) {
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 1, i, i);
            sheet.addMergedRegion(mergedRegion);
            RegionUtil.setBorderTop(BorderStyle.THIN, mergedRegion, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, mergedRegion, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegion, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegion, sheet);

        }
        String[] headers = {"STT", "Mã NV", "Tên nhân viên", "Chức vụ", "Mức lương cơ bản"};
        for (int i = 0; i < 5; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        for (int i = 0; i < 5; i++) {
            sheet.setColumnWidth(i, 5000);
        }
    }

    private void createHeaderTemplateLines(Long templateId, Map<String, List<Long>> indexTemplateLineMap
            , Sheet sheet, Row headerRow, CellStyle headerStyle) {
        List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities = salaryTemplatesSalaryColumnsRepository
                .findAllBySalaryTemplatesId(templateId);
        // lay ra groupId cua cac templateLine co group
        List<Long> groupIds = new ArrayList<>();
        salaryTemplatesSalaryColumnsEntities.forEach(
                salaryTemplatesSalaryColumnsEntity -> {
                    if (Objects.nonNull(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                        groupIds.add(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId());
                    }
                }
        );
        // lay ra tat ca cac columnId theo templateId
        List<Long> columnIds = salaryTemplatesSalaryColumnsEntities.stream().map(
                SalaryTemplatesSalaryColumnsEntity::getSalaryColumnsId
        ).collect(Collectors.toList());
        Map<Long, GroupSalaryColumnsEntity> groupSalaryColumnsEntityMap = customRepository.getGroupSalaryColumnsEntityMap(groupIds);
        Map<Long, SalaryColumnsEntity> salaryColumnsEntityMap = customRepository.getSalaryColumnsEntityMap(columnIds);
        Set<Long> columnIdsSet = new HashSet<>(columnIds);
        Row headerItemRow = sheet.createRow(1);
        Map<Long, List<SalaryColumnBasicResponse>> salaryColumnBasicResponseMap = getSalaryColumnBasicMap(salaryTemplatesSalaryColumnsEntities, columnIdsSet);
        // Lay ra thu tu cua cac templateLineItem: co 3 cot trong G2 va 1 cot rieng C2:[ G2-[1,2,3], C2-[4]]
        indexTemplateLineMap.forEach(
                (key, templateItem) -> {
                    if (templateItem.size() != 1) {
                        Long startIndex = templateItem.get(0);
                        Long endIndex = templateItem.get(templateItem.size() - 1);
                        CellRangeAddress mergedRegion = new CellRangeAddress(0, 0,
                                Math.toIntExact(startIndex) + 4, Math.toIntExact(endIndex) + 4);
                        sheet.addMergedRegion(mergedRegion);
                        Cell groupCell = headerRow.createCell(Math.toIntExact(startIndex) + 4);
                        Long idGroup = Long.parseLong(key.substring(1).trim());
                        groupCell.setCellValue(
                                groupSalaryColumnsEntityMap.get(idGroup)
                                        .getName()
                        );
                        groupCell.setCellStyle(headerStyle);
                        sheet.setColumnWidth(Math.toIntExact(startIndex) + 4, 5000 * templateItem.size());
                        //set Item column for groupTemplate
                        Long idColumn = Long.parseLong(key.substring(1).trim());
                        List<SalaryColumnBasicResponse> salaryColumnBasicResponses = salaryColumnBasicResponseMap.get(idColumn);
                        for (int i = 0; i < templateItem.size(); i++) {
                            Cell cellItem = headerItemRow.createCell(Math.toIntExact(templateItem.get(i)) + 4);
                            sheet.setColumnWidth(Math.toIntExact(templateItem.get(i)) + 4, 5000);
                            cellItem.setCellValue(
                                    salaryColumnBasicResponses.get(i).getName()
                            );
                            cellItem.setCellStyle(headerStyle);
                        }
                    } else {
                        Long index = templateItem.get(0);
                        CellRangeAddress mergedRegion = new CellRangeAddress(0, 1,
                                Math.toIntExact(index) + 4, Math.toIntExact(index) + 4);
                        sheet.addMergedRegion(mergedRegion);
                        Cell columnItemCell = headerRow.createCell(Math.toIntExact(index) + 4);
                        sheet.setColumnWidth(Math.toIntExact(Math.toIntExact(index)) + 4, 6000);
                        Long idColumn = Long.parseLong(key.substring(1).trim());
                        columnItemCell.setCellValue(
                                salaryColumnsEntityMap.get(idColumn)
                                        .getName()
                        );
                        columnItemCell.setCellStyle(headerStyle);
                        RegionUtil.setBorderTop(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegion, sheet);
                    }
                }
        );
    }
    private List<DataEmployeeResponse> getEmployeesExport(Long templateId) {
        SalaryTemplatesEntity salaryTemplatesEntity = customRepository.getSalaryTemplateEntityById(templateId);
        if (salaryTemplatesEntity.getApplicableType().equals(ApplicablesType.ALL)) {
            Map<Long, DataEmployeeResponse> dataEmployeeResponsesMap = serviceProxyImpl.getAllEmployeeMap();
            return new ArrayList<>(dataEmployeeResponsesMap.values());
        }
        Map<Long, List<DataEmployeeResponse>> employeeFromResourceMap = getEmployeeFromResourceMap(templateId);
        return employeeFromResourceMap.get(templateId);
    }

    private void createTemplateLines(SalaryTemplateRequest salaryTemplateRequest,
                                     SalaryTemplatesEntity salaryTemplatesEntity) {
        salaryTemplateApplicableRepository.deleteAllBySalaryTemplatesId(salaryTemplatesEntity.getId());
        salaryTemplatesSalaryColumnsRepository.deleteAllBySalaryTemplatesId(salaryTemplatesEntity.getId());
        if (Objects.isNull(salaryTemplateRequest.getTemplatesLineItems())) {
            return;
        }
        salaryTemplateRequest.getTemplatesLineItems().forEach(templatesLineRequest -> {
            if (Objects.isNull(templatesLineRequest.getSalaryColumn()) && Objects.isNull(templatesLineRequest.getGroupSalary()))
                throw new RuntimeException(ErrorCode.IS_NULL);
            if (Objects.nonNull(templatesLineRequest.getSalaryColumn()) && Objects.nonNull(templatesLineRequest.getGroupSalary()))
                throw new RuntimeException(ErrorCode.IS_NULL);
            // check column null-group not null
            if (Objects.isNull(templatesLineRequest.getSalaryColumn())) {
                List<SalaryColumnBasicRequest> groupSalaryItems = templatesLineRequest.getGroupSalaryItems();
                // check groupLineItems null
                if (Objects.isNull(groupSalaryItems) || groupSalaryItems.isEmpty())
                    throw new RuntimeException(ErrorCode.IS_NULL);
                groupSalaryItems.forEach(salaryColumnBasicRequest ->
                        salaryTemplatesSalaryColumnsRepository.save(SalaryTemplatesSalaryColumnsEntity.builder()
                                .salaryTemplatesId(salaryTemplatesEntity.getId())
                                .groupSalaryColumnsId(templatesLineRequest.getGroupSalary().getId())
                                .salaryColumnsId(salaryColumnBasicRequest.getId())
                                .build())
                );
            } else {
                salaryTemplatesSalaryColumnsRepository.save(
                        SalaryTemplatesSalaryColumnsEntity.builder()
                                .salaryTemplatesId(salaryTemplatesEntity.getId())
                                .salaryColumnsId(templatesLineRequest.getSalaryColumn().getId())
                                .build()
                );
            }
        });
    }

    private void createTeamPlateApplicableType(SalaryTemplateRequest salaryTemplateRequest,
                                               SalaryTemplatesEntity salaryTemplatesEntity) {
        // create salaryTemplateApplicable map
        if (salaryTemplateRequest.getApplicableType().equals(ApplicablesType.DEPARTMENT)) {
            if (Objects.isNull(salaryTemplateRequest.getDepartment())) throw new RuntimeException(ErrorCode.IS_NULL);
            List<Long> departmentIds = salaryTemplateRequest.getDepartment().stream()
                    .map(IdAndName::getId).collect(Collectors.toList());
            for (Long departmentId : departmentIds) {
                salaryTemplateApplicableRepository.save(SalaryTemplateApplicablesEntity.builder()
                        .salaryTemplatesId(salaryTemplatesEntity.getId())
                        .departmentId(departmentId)
                        .build());
            }
        } else if (salaryTemplateRequest.getApplicableType().equals(ApplicablesType.EMPLOYEE)) {
            if (Objects.isNull(salaryTemplateRequest.getEmployee())) throw new RuntimeException(ErrorCode.IS_NULL);
            List<Long> employeeIds = salaryTemplateRequest.getEmployee().stream()
                    .map(IdAndName::getId).collect(Collectors.toList());
            for (Long employeeId : employeeIds) {
                salaryTemplateApplicableRepository.save(SalaryTemplateApplicablesEntity.builder()
                        .salaryTemplatesId(salaryTemplatesEntity.getId())
                        .staffId(employeeId)
                        .build());
            }
        }
    }

    private Map<Long, List<DataEmployeeResponse>> getEmployeeFromResourceMap(Long templateId) {
        List<Long> employeeIds = salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(templateId).stream()
                .map(SalaryTemplateApplicablesEntity::getStaffId).collect(Collectors.toList());
        Map<Long, DataEmployeeResponse> employeeResponseMap = serviceProxyImpl.getEmployeesByListIdsMap(employeeIds);
        // Map salaryId - [employee]
        return salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(templateId).stream()
                .collect(Collectors.groupingBy(
                        SalaryTemplateApplicablesEntity::getSalaryTemplatesId,
                        Collectors.mapping(
                                salaryTemplateApplicablesEntity -> employeeResponseMap
                                        .get(salaryTemplateApplicablesEntity.getStaffId()), Collectors.toList()
                        )
                ));
    }

    Page<SalaryTemplatesEntity> getSalaryTemplateEntitySearch(
            Long id,
            String search,
            String name,
            Pageable pageable) {
        Specification<SalaryTemplatesEntity> conditions = Specification.where(SpecificationSalaryTemplate.filterById(id));
        conditions = conditions.and(SpecificationSalaryTemplate.filterNameOrCode(search));
        conditions = conditions.and(SpecificationSalaryTemplate.filterName(name));

        return salaryTemplateRepository.findAll(conditions, pageable);
    }

    private SalaryTemplateDetailResponse getSalaryTemplate(Long templateId
            , List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities
            , SalaryTemplateDetailResponse salaryTemplateResponse) {
        Map<Long, List<DataDepartmentResponse>> departmentFromResourceMap = getDepartmentFromResourceMap(templateId);
        Map<Long, List<DataEmployeeResponse>> employeeFromResourceMap = getEmployeeFromResourceMap(templateId);
        Set<Long> columnIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();
        Set<Long> columnItemIds = new HashSet<>();
        Set<Long> checkGroupExits = new HashSet<>();
        salaryTemplatesSalaryColumnsEntities.forEach(
                salaryTemplatesSalaryColumnsEntity -> {
                    if (Objects.isNull(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                        columnIds.add(salaryTemplatesSalaryColumnsEntity.getSalaryColumnsId());
                    } else {
                        groupIds.add(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId());
                        columnItemIds.add(salaryTemplatesSalaryColumnsEntity.getSalaryColumnsId());
                    }
                }
        );
        Map<Long, List<SalaryColumnBasicResponse>> salaryColumnBasicMap =
                getSalaryColumnBasicMap(salaryTemplatesSalaryColumnsEntities, columnItemIds);
        Map<Long, SalaryColumnBasicResponse> salaryColumnsBasicMap = salaryColumnsRepository.findAllByIdIn(columnIds)
                .stream().collect(Collectors.toMap(SalaryColumnsEntity::getId, salaryColumnsEntity ->
                        salaryColumnsMapper.getSalaryColumnBasicResponseFromEntity(salaryColumnsEntity)));
        Map<Long, GroupSalaryTemplateResponse> salaryGroupTemplateMap = groupSalaryColumnRepository
                .findAllByIdIn(groupIds)
                .stream().collect(Collectors.toMap(GroupSalaryColumnsEntity::getId, groupSalaryColumnsEntity ->
                        groupSalaryColumnMapper.getGroupTemplateResponse(groupSalaryColumnsEntity)));
        List<TemplateSalaryItems> templateSalaryItems = new ArrayList<>();
        salaryTemplatesSalaryColumnsEntities.forEach(
                salaryTemplatesSalaryColumnsEntity -> {
                    if (Objects.isNull(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                        TemplateSalaryItems templateSalaryItem = TemplateSalaryItems.builder()
                                .salaryColumn(salaryColumnsBasicMap
                                        .get(salaryTemplatesSalaryColumnsEntity.getSalaryColumnsId()))
                                .build();
                        templateSalaryItems.add(templateSalaryItem);
                    } else {
                        if (!checkGroupExits.contains(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                            TemplateSalaryItems templateSalaryItem = TemplateSalaryItems.builder()
                                    .groupSalaryItems(salaryColumnBasicMap.get(
                                            salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId()
                                    ))
                                    .groupSalaryColumn(salaryGroupTemplateMap.get(
                                            salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId()
                                    ))
                                    .build();
                            templateSalaryItems.add(templateSalaryItem);
                            checkGroupExits.add(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId());
                        }
                    }
                }
        );
        salaryTemplateResponse.setSalaryTemplateItems(templateSalaryItems);
        salaryTemplateResponse.setDepartment(departmentFromResourceMap.get(templateId));
        salaryTemplateResponse.setEmployee(employeeFromResourceMap.get(templateId));
        return salaryTemplateResponse;
    }

    private Map<Long, List<SalaryColumnBasicResponse>> getSalaryColumnBasicMap(
            List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities,
            Set<Long> columnIds) {
        List<Long> columnIdList = new ArrayList<>(columnIds);
        Map<Long, SalaryColumnsEntity> salaryColumnEntityMap = customRepository.getSalaryColumnsEntityMap(columnIdList);
        Map<Long, SalaryColumnBasicResponse> salaryColumnBasicResponseMap = new HashMap<>();
        salaryColumnEntityMap.forEach((id, salaryColumnsEntity)
                -> salaryColumnBasicResponseMap
                .put(id, salaryColumnsMapper.getSalaryColumnBasicResponseFromEntity(salaryColumnsEntity)));
        return salaryTemplatesSalaryColumnsEntities.stream()
                .filter(salaryTemplatesSalaryColumnsEntity ->
                        Objects.nonNull(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId()))
                .collect(Collectors.groupingBy(
                        SalaryTemplatesSalaryColumnsEntity::getGroupSalaryColumnsId,
                        Collectors.mapping(salaryTemplatesSalaryColumnsEntity ->
                                        salaryColumnBasicResponseMap.get(
                                                salaryTemplatesSalaryColumnsEntity.getSalaryColumnsId()
                                        ),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<DataDepartmentResponse>> getDepartmentFromResourceMap(
            Long salaryTemplateId) {
        List<Long> departmentIds = salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(salaryTemplateId)
                .stream().map(SalaryTemplateApplicablesEntity::getDepartmentId).collect(Collectors.toList());
        Map<Long, DataDepartmentResponse> departmentResponseMap = serviceProxyImpl.getDepartmentsByListIdsMap(departmentIds);
        return salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(salaryTemplateId).stream()
                .collect(Collectors.groupingBy(
                        SalaryTemplateApplicablesEntity::getSalaryTemplatesId,
                        Collectors.mapping(
                                salaryTemplateApplicablesEntity -> departmentResponseMap
                                        .get(salaryTemplateApplicablesEntity.getDepartmentId()), Collectors.toList()
                        )
                ));
    }

    private void checkExitRecord(SalaryTemplateRequest salaryTemplateRequest) {
        if (salaryTemplateRepository.existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                salaryTemplateRequest.getName(), salaryTemplateRequest.getCode()
        )) throw new RuntimeException(ErrorCode.SAME_RECORD);
    }

    private Map<String, List<Long>> getStartEndIndex(List<SalaryTemplatesSalaryColumnsEntity> columnRecords) {
        Map<String, List<Long>> indexGroupMap = new LinkedHashMap<>();
        Set<String> checkContain = new HashSet<>();
        Long index = 0L;
        for (SalaryTemplatesSalaryColumnsEntity record : columnRecords) {
            index++;
            if (Objects.nonNull(record.getGroupSalaryColumnsId())) {
                if (!checkContain.contains("G" + record.getGroupSalaryColumnsId())) {
                    indexGroupMap.put("G" + record.getGroupSalaryColumnsId(), new ArrayList<>());
                    checkContain.add("G" + record.getGroupSalaryColumnsId());
                }
                indexGroupMap.get("G" + record.getGroupSalaryColumnsId()).add(index);
            } else {
                indexGroupMap.put("C" + record.getSalaryColumnsId(), Collections.singletonList(index));
            }
        }
        return indexGroupMap;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFont(headerFont);
        //set color
        headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // setBorder
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        return headerStyle;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        return dataStyle;
    }
}