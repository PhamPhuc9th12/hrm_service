package org.example.hrm_salary.core.port.mapper;

import org.example.hrm_salary.app.dto.request.SalaryTemplateRequest.SalaryTemplateRequest;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateDetailResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateResponse;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface SalaryTemplateMapper {

    SalaryTemplatesEntity getEntityFromRequest(SalaryTemplateRequest salaryTemplateRequest);

    void updateSalaryTemplate(@MappingTarget SalaryTemplatesEntity salaryTemplatesEntity,
                              SalaryTemplateRequest salaryTemplateRequest
    );
    SalaryTemplateDetailResponse getResponseDetailFromEntity(SalaryTemplatesEntity salaryTemplatesEntity);
    SalaryTemplateResponse getResponseFromEntity(SalaryTemplatesEntity salaryTemplatesEntity);
}
