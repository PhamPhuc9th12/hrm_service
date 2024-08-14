package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryTemplateApplicablesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalaryTemplateApplicableRepository extends JpaRepository<SalaryTemplateApplicablesEntity, Long> {

    void deleteAllBySalaryTemplatesId(Long salaryTemplateId);

    List<SalaryTemplateApplicablesEntity> findAllBySalaryTemplatesId(Long salaryTemplateId);
    Long countAllBySalaryTemplatesIdIn(Collection<Long> ids);
    List<SalaryTemplateApplicablesEntity> findAllBySalaryTemplatesIdIn(Collection<Long> salaryTemplateId);

}
