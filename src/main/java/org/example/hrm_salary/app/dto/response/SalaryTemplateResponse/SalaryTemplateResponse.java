package org.example.hrm_salary.app.dto.response.SalaryTemplateResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnBasicRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnBasicResponse;
import org.example.hrm_salary.app.dto.response.department.DataDepartmentResponse;
import org.example.hrm_salary.app.dto.response.employee.DataEmployeeResponse;
import org.example.hrm_salary.core.domain.enums.ApplicablesType;
import org.example.hrm_salary.core.domain.enums.LoopType;
import org.example.hrm_salary.core.domain.enums.TemplateType;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryTemplateResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private TemplateType templateType;
    private ApplicablesType applicableType;
    private Long numberOfApplicable;
}
