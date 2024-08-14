package org.example.hrm_salary.core.port.mapper;

import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnUpdateRequest;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnsCreateRequest;
import org.example.hrm_salary.app.dto.response.GroupSalaryColumnResponse.GroupSalaryColumnResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.GroupSalaryTemplateResponse;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface GroupSalaryColumnMapper {

    GroupSalaryColumnsEntity getGroupSalaryColumnEntityBy(GroupSalaryColumnsCreateRequest groupSalaryColumnsCreateRequest);

    GroupSalaryColumnResponse getResponseGroupSalaryBy(GroupSalaryColumnsEntity groupSalaryColumnsEntity);

    GroupSalaryTemplateResponse getGroupTemplateResponse(GroupSalaryColumnsEntity groupSalaryColumnsEntity);

    void updateGroupSalaryColumns(@MappingTarget GroupSalaryColumnsEntity groupSalaryColumnsEntity,
                                  GroupSalaryColumnUpdateRequest groupSalaryColumnUpdateRequest);
}
