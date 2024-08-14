package org.example.hrm_salary.app.dto.request.SalaryTemplateRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnBasicRequest;
import org.example.hrm_salary.core.domain.constant.IdAndName;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemplatesLineRequest {
    IdAndName groupSalary;
    SalaryColumnBasicRequest salaryColumn;
    List<SalaryColumnBasicRequest> groupSalaryItems;
}
