package org.example.hrm_salary.app.dto.request.SalarayColumnRequest;

import lombok.*;
import org.example.hrm_salary.core.domain.enums.IncomeType;
import org.example.hrm_salary.core.domain.enums.SalaryType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SalaryColumnRequest {
    private Long groupSalaryColumnsId;
    @NotNull
    private String code;
    @NotNull
    private String name;
    private SalaryType columnType;
    private IncomeType incomeType;
}
