package org.example.hrm_salary.core.port.repository;
import org.example.hrm_salary.core.domain.entity.GroupSalaryColumnsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GroupSalaryColumnRepository extends JpaRepository<GroupSalaryColumnsEntity, Long>,
        JpaSpecificationExecutor<GroupSalaryColumnsEntity> {

    Page<GroupSalaryColumnsEntity> findAllByNameOrCode(String name, String code, Pageable pageable);

    Boolean existsByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);

    Boolean existsByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(String name, String code);

    List<GroupSalaryColumnsEntity> findAllByIdIn(Collection<Long> groupIds);


}
