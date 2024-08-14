package org.example.hrm_salary.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hrm_salary.core.domain.enums.IncomeType;
import org.example.hrm_salary.core.domain.enums.SalaryType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "salary_columns")
public class SalaryColumnsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long groupSalaryColumnsId;
    @NotNull
    private String code;
    @NotNull
    private String name;
    @Enumerated(EnumType.STRING)
    private SalaryType columnType;
    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;
}
