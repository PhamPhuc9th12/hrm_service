package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalaryTemplateRepository extends JpaRepository<SalaryTemplatesEntity, Long>, JpaSpecificationExecutor<SalaryTemplatesEntity> {
    Boolean existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
