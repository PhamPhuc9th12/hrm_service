package org.example.hrm_salary.app.dto.response.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {
    List<DataEmployeeResponse> data;
}
