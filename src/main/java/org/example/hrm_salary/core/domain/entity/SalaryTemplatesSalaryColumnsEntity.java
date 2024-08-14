package org.example.hrm_salary.core.domain.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name ="salary_templates_salary_columns" )

public class SalaryTemplatesSalaryColumnsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long salaryTemplatesId;
    private Long groupSalaryColumnsId;
    private Long salaryColumnsId;
}
