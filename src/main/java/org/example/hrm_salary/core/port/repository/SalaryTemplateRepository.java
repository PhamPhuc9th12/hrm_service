package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryTemplateRepository extends JpaRepository<SalaryTemplatesEntity, Long> {
    Boolean existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
