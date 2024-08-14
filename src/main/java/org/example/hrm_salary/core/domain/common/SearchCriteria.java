package org.example.hrm_salary.core.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchCriteria {
    private String key;
    private String operation;
    private String value;

}
