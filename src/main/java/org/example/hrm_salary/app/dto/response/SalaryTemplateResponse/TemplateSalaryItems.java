package org.example.hrm_salary.app.dto.response.SalaryTemplateResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnBasicRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnBasicResponse;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnsResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateSalaryItems {
    private GroupSalaryTemplateResponse groupSalaryColumn;
    private SalaryColumnBasicResponse salaryColumn;
    private List<SalaryColumnBasicResponse> groupSalaryItems;
}
