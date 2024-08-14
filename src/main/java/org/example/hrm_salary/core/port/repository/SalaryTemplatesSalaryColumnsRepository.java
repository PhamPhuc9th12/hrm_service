package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesSalaryColumnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalaryTemplatesSalaryColumnsRepository extends JpaRepository<SalaryTemplatesSalaryColumnsEntity, Long> ,
        JpaSpecificationExecutor<SalaryTemplatesEntity> {

    void deleteAllBySalaryTemplatesId(Long salaryTemplateId);
    void deleteAllBySalaryColumnsId(Long salaryColumnId);
    void deleteAllByGroupSalaryColumnsId(Long groupColumnId);
    List<SalaryTemplatesSalaryColumnsEntity> findAllBySalaryTemplatesId(Long salaryTemplateId);

    List<SalaryTemplatesSalaryColumnsEntity> findAllBySalaryTemplatesIdIn(Collection<Long> salaryTemplateIds);
}
