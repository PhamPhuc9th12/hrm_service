package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalaryColumnsRepository extends JpaRepository<SalaryColumnsEntity, Long> {

    Boolean existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);

    Page<SalaryColumnsEntity> findAllByGroupSalaryColumnsId(Long id, Pageable pageable);

    List<SalaryColumnsEntity> findAllByIdIn(Collection<Long> columnIds);

}
