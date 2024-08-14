package org.example.hrm_salary.core.service;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.app.api.SalaryColumnApi;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnsResponse;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.constant.IdAndName;
import org.example.hrm_salary.core.domain.constant.IdResponse;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.port.mapper.SalaryColumnsMapper;
import org.example.hrm_salary.core.port.repository.CustomRepository;
import org.example.hrm_salary.core.port.repository.GroupSalaryColumnRepository;
import org.example.hrm_salary.core.port.repository.SalaryColumnsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class SalaryColumnsService implements SalaryColumnApi {

    private final CustomRepository customRepository;
    private final SalaryColumnsRepository salaryColumnsRepository;
    private final SalaryColumnsMapper salaryColumnsMapper;
    private final GroupSalaryColumnRepository groupSalaryColumnRepository;

    @Transactional
    @Override
    public IdResponse createSalaryColumns(SalaryColumnRequest salaryColumnRequest) {
        checkExitRecord(salaryColumnRequest);
        SalaryColumnsEntity salaryColumnsEntity = salaryColumnsMapper.getSalaryColumnsEntityByRequest(salaryColumnRequest);
        salaryColumnsRepository.save(salaryColumnsEntity);
        return IdResponse.builder()
                .id(salaryColumnsEntity.getId())
                .build();
    }

    @Override
    @Transactional
    public IdResponse updateSalaryColumns(Long salaryColumnId, SalaryColumnRequest salaryColumnRequest) {
        checkExitRecord(salaryColumnRequest);
        SalaryColumnsEntity salaryColumnsEntity = customRepository.getSalaryColumnEntityById(salaryColumnId);
        salaryColumnsMapper.updateSalaryColumnsEntityByRequest(salaryColumnsEntity, salaryColumnRequest);
        salaryColumnsRepository.save(salaryColumnsEntity);
        return IdResponse.builder()
                .id(salaryColumnsEntity.getId())
                .build();
    }

    @Override
    public Page<SalaryColumnsResponse> getSalaryColumns(Pageable pageable) {
        Page<SalaryColumnsEntity> salaryColumnsEntities = salaryColumnsRepository.findAll(pageable);
        if (salaryColumnsEntities.isEmpty()) throw new RuntimeException(ErrorCode.NOT_FOUND);
        List<Long> groupIds = salaryColumnsEntities.stream().map(SalaryColumnsEntity::getGroupSalaryColumnsId)
                .collect(Collectors.toList());
        Map<Long, String> groupSalaryIdNameMap = groupSalaryColumnRepository.findAllByIdIn(groupIds).stream()
                .collect(Collectors.toMap(GroupSalaryColumnsEntity::getId, GroupSalaryColumnsEntity::getName));
        return salaryColumnsEntities.map(
                salaryColumnsEntity -> {
                    SalaryColumnsResponse salaryColumnsResponse = salaryColumnsMapper.
                            getSalaryColumnResponseFromEntity(salaryColumnsEntity);
                    salaryColumnsResponse.setGroupSalaryColumnInformation(IdAndName.builder()
                            .id(salaryColumnsEntity.getGroupSalaryColumnsId())
                            .name(groupSalaryIdNameMap.get(salaryColumnsEntity.getGroupSalaryColumnsId()))
                            .build());
                    return salaryColumnsResponse;
                }
        );
    }

    @Override
    public SalaryColumnsResponse getSalaryColumnsById(Long salaryColumnId) {
        SalaryColumnsEntity salaryColumnsEntity = customRepository.getSalaryColumnEntityById(salaryColumnId);
        SalaryColumnsResponse salaryColumnsResponse = salaryColumnsMapper.
                getSalaryColumnResponseFromEntity(salaryColumnsEntity);
        salaryColumnsResponse.setGroupSalaryColumnInformation(
                IdAndName.builder()
                        .id(salaryColumnsEntity.getGroupSalaryColumnsId())
                        .name(customRepository.getGroupSalaryEntityById(
                                salaryColumnsEntity.getGroupSalaryColumnsId()).getName()
                        )
                        .build()
        );
        return salaryColumnsResponse;
    }

    @Override
    @Transactional
    public void deleteSalaryColumn(Long salaryColumnId) {
        SalaryColumnsEntity salaryColumnsEntity = customRepository.getSalaryColumnEntityById(salaryColumnId);
        salaryColumnsRepository.delete(salaryColumnsEntity);
    }

    @Override
    public Page<SalaryColumnsResponse> getListSalaryColumnByGroupId(Long groupId, Pageable pageable) {
        Page<SalaryColumnsEntity> salaryColumnsEntities = salaryColumnsRepository
                .findAllByGroupSalaryColumnsId(groupId,pageable);
        if(salaryColumnsEntities.isEmpty()) throw new RuntimeException(ErrorCode.NOT_FOUND);
        GroupSalaryColumnsEntity groupSalaryColumnsEntity = customRepository.getGroupSalaryEntityById(groupId);
        IdAndName groupSalaryInformation = IdAndName.builder()
                .id(groupId)
                .name(groupSalaryColumnsEntity.getName())
                .build();
        return salaryColumnsEntities.map(
                salaryColumnsEntity -> {
                    SalaryColumnsResponse salaryColumnsResponse = salaryColumnsMapper
                            .getSalaryColumnResponseFromEntity(salaryColumnsEntity);
                    salaryColumnsResponse.setGroupSalaryColumnInformation(groupSalaryInformation);
                    return salaryColumnsResponse;
                }
        );
    }

    private void checkExitRecord(SalaryColumnRequest salaryColumnRequest) {
        if (salaryColumnsRepository.existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                salaryColumnRequest.getName(), salaryColumnRequest.getCode()
        )) throw new RuntimeException(ErrorCode.SAME_RECORD);
    }

}
