package org.example.hrm_salary.app.dto.response.GroupSalaryColumnResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupSalaryColumnResponse {
    private Long id ;
    private String code;
    private String name;
    private String description;
    private String groupColumnType;
    private OffsetDateTime createdAt;
}
