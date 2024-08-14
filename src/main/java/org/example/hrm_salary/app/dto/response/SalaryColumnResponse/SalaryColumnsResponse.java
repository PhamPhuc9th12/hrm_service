package org.example.hrm_salary.app.dto.response.SalaryColumnResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.example.hrm_salary.core.domain.constant.IdAndName;
import org.example.hrm_salary.core.domain.enums.IncomeType;
import org.example.hrm_salary.core.domain.enums.SalaryType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryColumnsResponse {
    private Long id;
    private IdAndName groupSalaryColumnInformation;
    private String code;
    private String name;
    private SalaryType columnType;
    private IncomeType incomeType;
}
