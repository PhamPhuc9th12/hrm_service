package org.example.hrm_salary.app.dto.request.SalaryTemplateRequest;

import lombok.*;
import org.example.hrm_salary.core.domain.constant.IdAndName;
import org.example.hrm_salary.core.domain.enums.ApplicablesType;
import org.example.hrm_salary.core.domain.enums.LoopType;
import org.example.hrm_salary.core.domain.enums.TemplateType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SalaryTemplateRequest {

    @NotNull
    private String code;
    @NotNull
    private String name;
    private String description;
    private TemplateType templateType;
    private LoopType loopType;
    private OffsetDateTime start_date;
    private ApplicablesType applicableType;
    private List<IdAndName> employee;
    private List<IdAndName> department;
    private List<TemplatesLineRequest> templatesLineItems;
}
