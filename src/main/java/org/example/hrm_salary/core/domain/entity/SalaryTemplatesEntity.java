package org.example.hrm_salary.core.domain.entity;

import lombok.*;
import org.example.hrm_salary.core.domain.enums.ApplicablesType;
import org.example.hrm_salary.core.domain.enums.LoopType;
import org.example.hrm_salary.core.domain.enums.TemplateType;

import javax.persistence.*;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name ="salary_templates" )
public class SalaryTemplatesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;
    @Enumerated(EnumType.STRING)
    private LoopType loopType;
    private OffsetDateTime start_date;
    @Enumerated(EnumType.STRING)
    private ApplicablesType applicableType;
}
