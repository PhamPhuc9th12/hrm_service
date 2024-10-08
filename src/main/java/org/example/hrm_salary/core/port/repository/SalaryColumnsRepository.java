package org.example.hrm_salary.core.port.repository;

import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface SalaryColumnsRepository extends JpaRepository<SalaryColumnsEntity, Long>, JpaSpecificationExecutor<SalaryColumnsEntity> {

    Boolean existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);

    Page<SalaryColumnsEntity> findAllByGroupSalaryColumnsId(Long id, Pageable pageable);

    List<SalaryColumnsEntity> findAllByIdIn(Collection<Long> columnIds);
//    Map<Long, List<SalaryColumnsEntity>> findSalaryColumnsEntitiesB
}
