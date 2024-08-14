package org.example.hrm_salary.app.dto.request.SalarayColumnRequest;

import lombok.*;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.enums.IncomeType;
import org.example.hrm_salary.core.domain.enums.SalaryType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SalaryColumnRequest {
    private Long groupSalaryColumnsId;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "Username must not contain whitespace")
    @Size(max = 50, message = ErrorCode.MORE_THAN_FIFTY_CHARACTER)
    private String code;
    @Size(min = 5, message = ErrorCode.MORE_THAN_FIVE_CHARACTER)
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",message = ErrorCode.IS_SPECIAL_CHARACTER)
    private String name;
    private SalaryType columnType;
    private IncomeType incomeType;
}
