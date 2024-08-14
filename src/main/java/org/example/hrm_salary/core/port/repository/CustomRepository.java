package org.example.hrm_salary.core.port.repository;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Map<Long, GroupSalaryColumnsEntity> getGroupSalaryColumnsEntityMap(List<Long> ids){
        if(Objects.isNull(ids) || ids.isEmpty()){
            return new HashMap<>();
        }
        return groupSalaryColumnRepository.findAllByIdIn(ids).stream().collect(Collectors.toMap(
                GroupSalaryColumnsEntity::getId, Function.identity()
        ));
    }
    public Map<Long, SalaryColumnsEntity> getSalaryColumnsEntityMap(List<Long> ids){
        if(Objects.isNull(ids) || ids.isEmpty()){
            return new HashMap<>();
        }
        return salaryColumnsRepository.findAllByIdIn(ids).stream().collect(Collectors.toMap(
                SalaryColumnsEntity::getId, Function.identity()
        ));
    }
}
