package org.example.hrm_salary.app.dto.response.SalaryTemplateResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryTemplateDetailResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private TemplateType templateType;
    private LoopType loopType;
    private OffsetDateTime start_date;
    private ApplicablesType applicableType;
    private List<DataEmployeeResponse> employee;
    private List<DataDepartmentResponse> department;
    private List<TemplateSalaryItems> salaryTemplateItems;
}
