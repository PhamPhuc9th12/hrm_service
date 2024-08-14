package org.example.hrm_salary.app.dto.request.SalarayColumnRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrm_salary.core.domain.enums.GroupColumnTypeEnums;
import org.example.hrm_salary.core.domain.enums.IncomeType;
import org.example.hrm_salary.core.domain.enums.SalaryType;

import javax.validation.constraints.NotNull;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SalaryColumnBasicRequest {
    private Long id;
    @NotNull
    private String code;
    @NotNull
    private String name;
    private SalaryType columnType;

}
