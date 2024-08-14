package org.example.hrm_salary.core.service;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.app.api.GroupSalaryColumnsApi;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnUpdateRequest;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnsCreateRequest;
import org.example.hrm_salary.app.dto.response.GroupSalaryColumnResponse.GroupSalaryColumnResponse;
import org.example.hrm_salary.core.domain.common.SearchCriteria;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.example.hrm_salary.core.port.mapper.GroupSalaryColumnMapper;
import org.example.hrm_salary.core.port.repository.GroupSalaryColumnRepository;
import org.example.hrm_salary.core.domain.specification.GroupSalaryColumnSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GroupSalaryColumnService implements GroupSalaryColumnsApi {
    private final GroupSalaryColumnMapper groupSalaryColumnMapper;
    private final GroupSalaryColumnRepository groupSalaryColumnRepository;

    @Override
    @Transactional
    public void createGroupColumnsType(GroupSalaryColumnsCreateRequest groupSalaryColumnsCreateRequest) {
        checkSameRecordCreate(groupSalaryColumnsCreateRequest.getName(), groupSalaryColumnsCreateRequest.getCode());
        GroupSalaryColumnsEntity groupSalaryColumnsEntity = groupSalaryColumnMapper
                .getGroupSalaryColumnEntityBy(groupSalaryColumnsCreateRequest);

        groupSalaryColumnsEntity.setCreatedAt(OffsetDateTime.now());
        groupSalaryColumnRepository.save(groupSalaryColumnsEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupSalaryColumnResponse> getListGroupSalaryResponse(String name, String code, Pageable pageable) {
        Page<GroupSalaryColumnResponse> groupSalaryColumnResponsePage = null;
        GroupSalaryColumnSpecification nameSearch = (name != null && !name.isEmpty()) ? new GroupSalaryColumnSpecification(
                new SearchCriteria("name", ":", name)) : null;
        GroupSalaryColumnSpecification codeSeach = (code != null && !code.isEmpty()) ? new GroupSalaryColumnSpecification(
                new SearchCriteria("code", ":", code)) : null;

        if (Objects.nonNull(name) || Objects.nonNull(code)) {
            Page<GroupSalaryColumnsEntity> groupSalaryColumnsEntitiesSearch = groupSalaryColumnRepository
                    .findAll(Specification.where(nameSearch).or(codeSeach), pageable);
            groupSalaryColumnResponsePage = getListGroup(groupSalaryColumnsEntitiesSearch);
        } else {
            Page<GroupSalaryColumnsEntity> groupSalaryColumnsEntities = groupSalaryColumnRepository.findAll(pageable);
            groupSalaryColumnResponsePage = getListGroup(groupSalaryColumnsEntities);
        }

        return groupSalaryColumnResponsePage;
    }

    @Override
    @Transactional
    public void updateGroupColumnsType(GroupSalaryColumnUpdateRequest groupSalaryColumnUpdateRequest, Long id) {
        checkSameRecordCreate(groupSalaryColumnUpdateRequest.getName(), groupSalaryColumnUpdateRequest.getCode());
        GroupSalaryColumnsEntity groupSalaryColumnsEntity = groupSalaryColumnRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
        groupSalaryColumnMapper.updateGroupSalaryColumns(groupSalaryColumnsEntity, groupSalaryColumnUpdateRequest);

        groupSalaryColumnsEntity.setUpdatedAt(OffsetDateTime.now());
        groupSalaryColumnRepository.save(groupSalaryColumnsEntity);
    }

    @Override
    @Transactional
    public void deleteGroupColumnSalary(Long columnId) {
        groupSalaryColumnRepository.findById(columnId).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
        groupSalaryColumnRepository.deleteById(columnId);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupSalaryColumnResponse getColumnById(Long columnId) {
        GroupSalaryColumnsEntity groupSalaryColumnsEntity = groupSalaryColumnRepository.findById(columnId).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
        return groupSalaryColumnMapper.getResponseGroupSalaryBy(groupSalaryColumnsEntity);
    }

    private void checkSameRecordCreate(String name, String code) {
        if (Boolean.TRUE.equals(groupSalaryColumnRepository.
                existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(name, code)
        )) throw new RuntimeException(ErrorCode.SAME_RECORD);
    }

    private Page<GroupSalaryColumnResponse> getListGroup(Page<GroupSalaryColumnsEntity> groupSalaryColumnsEntities) {
        if (Boolean.TRUE.equals(groupSalaryColumnsEntities.isEmpty())) throw new RuntimeException(ErrorCode.NOT_FOUND);
        return groupSalaryColumnsEntities.map(groupSalaryColumnMapper::getResponseGroupSalaryBy);
    }
}
