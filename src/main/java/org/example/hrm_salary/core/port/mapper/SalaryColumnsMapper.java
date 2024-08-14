package org.example.hrm_salary.core.port.mapper;

import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnBasicRequest;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnBasicResponse;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnsResponse;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface SalaryColumnsMapper {
    SalaryColumnsEntity getSalaryColumnsEntityByRequest(SalaryColumnRequest salaryColumnRequest);

    void updateSalaryColumnsEntityByRequest(
            @MappingTarget SalaryColumnsEntity salaryColumnsEntity,
            SalaryColumnRequest salaryColumnRequest);

    SalaryColumnsResponse getSalaryColumnResponseFromEntity(SalaryColumnsEntity salaryColumnsEntity);
    SalaryColumnBasicResponse getSalaryColumnBasicResponseFromEntity(SalaryColumnsEntity salaryColumnsEntity);
}
