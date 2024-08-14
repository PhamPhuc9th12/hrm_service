package org.example.hrm_salary.core.service;

import lombok.AllArgsConstructor;
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
import org.example.hrm_salary.core.port.mapper.GroupSalaryColumnMapper;
import org.example.hrm_salary.core.port.mapper.SalaryColumnsMapper;
import org.example.hrm_salary.core.port.mapper.SalaryTemplateMapper;
import org.example.hrm_salary.core.port.proxy.ServiceProxy;
import org.example.hrm_salary.core.port.repository.*;
import org.example.hrm_salary.app.dto.response.employee.DataEmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
    public Page<SalaryTemplateResponse> getSalaryTemplatePage(Pageable pageable) {
        Page<SalaryTemplatesEntity> salaryTemplatesEntities = salaryTemplateRepository.findAll(pageable);
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
        List<SalaryTemplateApplicablesEntity> salaryTemplateApplicablesEntity = salaryTemplateApplicableRepository
                .findAllBySalaryTemplatesId(templateId);
        if (salaryTemplateApplicablesEntity.isEmpty()) throw new RuntimeException(ErrorCode.LIST_IS_EMPTY);
        List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities =
                salaryTemplatesSalaryColumnsRepository.findAllBySalaryTemplatesId(templateId);
        if (salaryTemplatesSalaryColumnsEntities.isEmpty()) throw new RuntimeException(ErrorCode.LIST_IS_EMPTY);
        return getSalaryTemplateInformationById(templateId, salaryTemplatesSalaryColumnsEntities, salaryTemplateResponse);
    }

    @Override
    @Transactional
    public IdResponse createSalaryTemplate(SalaryTemplateRequest salaryTemplateRequest) {
        checkExitRecord(salaryTemplateRequest);
        SalaryTemplatesEntity salaryTemplatesEntity = salaryTemplateMapper.getEntityFromRequest(salaryTemplateRequest);
        salaryTemplateRepository.save(salaryTemplatesEntity);
        // create salary
        createMapperDataSalaryTemplate(salaryTemplateRequest, salaryTemplatesEntity);
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
        createMapperDataSalaryTemplate(salaryTemplateRequest, salaryTemplatesEntity);
        return IdResponse.builder()
                .id(salaryTemplatesEntity.getId())
                .build();
    }

    private void createMapperDataSalaryTemplate(SalaryTemplateRequest salaryTemplateRequest,
                                                SalaryTemplatesEntity salaryTemplatesEntity) {
        salaryTemplateApplicableRepository.deleteAllBySalaryTemplatesId(salaryTemplatesEntity.getId());
        salaryTemplatesSalaryColumnsRepository.deleteAllBySalaryTemplatesId(salaryTemplatesEntity.getId());

        if (Objects.isNull(salaryTemplateRequest.getTemplatesLineItems())) {
            return;
        }
        salaryTemplateRequest.getTemplatesLineItems().forEach(templatesLineRequest -> {
            // check column null
            if (Objects.isNull(templatesLineRequest.getSalaryColumn())) {
                if (Objects.isNull(templatesLineRequest.getGroupSalary()))
                    throw new RuntimeException(ErrorCode.IS_NULL);
                List<SalaryColumnBasicRequest> salaryColumnBasicRequestList = templatesLineRequest.getGroupSalaryItems();
                if (Objects.isNull(salaryColumnBasicRequestList) || salaryColumnBasicRequestList.isEmpty())
                    throw new RuntimeException(ErrorCode.IS_NULL);
                salaryColumnBasicRequestList.forEach(
                        salaryColumnBasicRequest -> {
                            salaryTemplatesSalaryColumnsRepository.save(
                                    SalaryTemplatesSalaryColumnsEntity.builder()
                                            .salaryTemplatesId(salaryTemplatesEntity.getId())
                                            .groupSalaryColumnsId(templatesLineRequest.getGroupSalary().getId())
                                            .salaryColumnsId(salaryColumnBasicRequest.getId())
                                            .build()
                            );
                        }
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
        // create salaryTemplateApplicable map
        if (salaryTemplateRequest.getApplicableType().equals(ApplicablesType.DEPARTMENT)) {
            if (Objects.isNull(salaryTemplateRequest.getDepartment()))
                throw new RuntimeException(ErrorCode.IS_NULL);
            List<Long> departmentIds = salaryTemplateRequest.getDepartment().stream()
                    .map(IdAndName::getId).collect(Collectors.toList());
            for (Long departmentId : departmentIds) {
                salaryTemplateApplicableRepository.save(SalaryTemplateApplicablesEntity.builder()
                        .salaryTemplatesId(salaryTemplatesEntity.getId())
                        .departmentId(departmentId)
                        .build());
            }
        } else if (salaryTemplateRequest.getApplicableType().equals(ApplicablesType.EMPLOYEE)) {
            if (Objects.isNull(salaryTemplateRequest.getEmployee()))
                throw new RuntimeException(ErrorCode.IS_NULL);
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

    private Map<Long, List<DataEmployeeResponse>> salaryIdEmployeeInformationFromResource(
            Long templateId) {

        List<Long> employeeIds = salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(templateId).stream()
                .map(SalaryTemplateApplicablesEntity::getStaffId).collect(Collectors.toList());
        Map<Long, DataEmployeeResponse> idEmployeeResponseMap = serviceProxyImpl.getEmployeesByListIds(employeeIds)
                .getData().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DataEmployeeResponse::getId, Function.identity()));
        return salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(templateId).stream()
                .collect(Collectors.groupingBy(
                        SalaryTemplateApplicablesEntity::getSalaryTemplatesId,
                        Collectors.mapping(
                                salaryTemplateApplicablesEntity -> idEmployeeResponseMap
                                        .get(salaryTemplateApplicablesEntity.getStaffId()), Collectors.toList()
                        )
                ));
    }

    private SalaryTemplateDetailResponse getSalaryTemplateInformationById(Long templateId
            , List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities
            , SalaryTemplateDetailResponse salaryTemplateResponse) {
        Map<Long, List<DataDepartmentResponse>> salaryIdDepartmentInformationFromResource =
                salaryIdDepartmentInformationFromResource(templateId);
        Map<Long, List<DataEmployeeResponse>> salaryIdEmployeeInformationFromResource =
                salaryIdEmployeeInformationFromResource(templateId);
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
        Map<Long, List<SalaryColumnBasicResponse>> groupIdSalaryColumnBasicMap =
                groupIdSalaryColumnBasicMap(salaryTemplatesSalaryColumnsEntities, columnItemIds);

        Map<Long, SalaryColumnBasicResponse> longSalaryColumnsEntityMap = salaryColumnsRepository
                .findAllByIdIn(columnIds)
                .stream().collect(Collectors.toMap(SalaryColumnsEntity::getId, salaryColumnsEntity ->
                        salaryColumnsMapper.getSalaryColumnBasicResponseFromEntity(salaryColumnsEntity)));
        Map<Long, GroupSalaryTemplateResponse> groupSalaryColumnsEntityMap = groupSalaryColumnRepository
                .findAllByIdIn(groupIds)
                .stream().collect(Collectors.toMap(GroupSalaryColumnsEntity::getId, groupSalaryColumnsEntity ->
                        groupSalaryColumnMapper.getGroupTemplateResponse(groupSalaryColumnsEntity)));
        List<TemplateSalaryItems> templateSalaryItems = new ArrayList<>();
        salaryTemplatesSalaryColumnsEntities.forEach(
                salaryTemplatesSalaryColumnsEntity -> {
                    if (Objects.isNull(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                        TemplateSalaryItems templateSalaryItem = TemplateSalaryItems.builder()
                                .salaryColumn(longSalaryColumnsEntityMap
                                        .get(salaryTemplatesSalaryColumnsEntity.getSalaryColumnsId()))
                                .build();
                        templateSalaryItems.add(templateSalaryItem);
                    } else {
                        if (!checkGroupExits.contains(salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId())) {
                            TemplateSalaryItems templateSalaryItem = TemplateSalaryItems.builder()
                                    .groupSalaryItems(groupIdSalaryColumnBasicMap.get(
                                            salaryTemplatesSalaryColumnsEntity.getGroupSalaryColumnsId()
                                    ))
                                    .groupSalaryColumn(groupSalaryColumnsEntityMap.get(
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
        salaryTemplateResponse.setDepartment(salaryIdDepartmentInformationFromResource.get(templateId));
        salaryTemplateResponse.setEmployee(salaryIdEmployeeInformationFromResource.get(templateId));
        return salaryTemplateResponse;
    }

    private Map<Long, List<SalaryColumnBasicResponse>> groupIdSalaryColumnBasicMap(
            List<SalaryTemplatesSalaryColumnsEntity> salaryTemplatesSalaryColumnsEntities,
            Set<Long> columnIds) {
        Map<Long, SalaryColumnBasicResponse> salaryColumnBasicResponseMap = salaryColumnsRepository.findAllByIdIn(columnIds)
                .stream()
                .collect(Collectors.toMap(
                        SalaryColumnsEntity::getId,
                        salaryColumnsEntity -> salaryColumnsMapper.getSalaryColumnBasicResponseFromEntity(salaryColumnsEntity)
                ));

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

    private Map<Long, List<DataDepartmentResponse>> salaryIdDepartmentInformationFromResource(
            Long salaryTemplateId) {
        List<Long> departmentIds = salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(salaryTemplateId)
                .stream().map(SalaryTemplateApplicablesEntity::getDepartmentId).collect(Collectors.toList());
        Map<Long, DataDepartmentResponse> idDepartmentResponseMap = serviceProxyImpl
                .getDepartmentsByListIds(departmentIds).getContent()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DataDepartmentResponse::getId, Function.identity()));
        return salaryTemplateApplicableRepository.findAllBySalaryTemplatesId(salaryTemplateId).stream()
                .collect(Collectors.groupingBy(
                        SalaryTemplateApplicablesEntity::getSalaryTemplatesId,
                        Collectors.mapping(
                                salaryTemplateApplicablesEntity -> idDepartmentResponseMap
                                        .get(salaryTemplateApplicablesEntity.getDepartmentId()), Collectors.toList()
                        )
                ));
    }

    private void checkExitRecord(SalaryTemplateRequest salaryTemplateRequest) {
        if (salaryTemplateRepository.existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                salaryTemplateRequest.getName(), salaryTemplateRequest.getCode()
        )) throw new RuntimeException(ErrorCode.SAME_RECORD);
    }

}