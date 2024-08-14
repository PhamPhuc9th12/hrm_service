package org.example.hrm_salary.core.port.repository;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {

    private final  GroupSalaryColumnRepository groupSalaryColumnRepository;
    private final  SalaryColumnsRepository salaryColumnsRepository;
    private final SalaryTemplateRepository salaryTemplateRepository;
    public GroupSalaryColumnsEntity getGroupSalaryEntityById(Long id){
        return groupSalaryColumnRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
    }

    public SalaryColumnsEntity getSalaryColumnEntityById(Long id){
        return salaryColumnsRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
    }

    public SalaryTemplatesEntity getSalaryTemplateEntityById(Long id){
        return salaryTemplateRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND)
        );
    }
}
