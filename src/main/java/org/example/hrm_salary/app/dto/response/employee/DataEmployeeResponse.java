package org.example.hrm_salary.app.dto.response.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.hrm_salary.core.domain.constant.IdAndName;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataEmployeeResponse {
    private Long id;
    private String code;
    private String lastName;
    private String firstName;
    private IdAndName position;
    private Boolean activated;
}
