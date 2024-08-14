package org.example.hrm_salary.core.domain.entity;

import lombok.*;
import org.example.hrm_salary.core.domain.enums.GroupColumnTypeEnums;

import javax.persistence.*;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "group_salary_columns")
public class GroupSalaryColumnsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String code;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private GroupColumnTypeEnums groupColumnType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
