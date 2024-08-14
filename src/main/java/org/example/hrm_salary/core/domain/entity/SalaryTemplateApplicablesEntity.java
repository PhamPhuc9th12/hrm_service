package org.example.hrm_salary.core.domain.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "salary_template_applicables")
@Builder
public class SalaryTemplateApplicablesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long salaryTemplatesId;
    private Long departmentId;
    private Long staffId;
}
