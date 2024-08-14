package org.example.hrm_salary.core.domain.specification;

import org.example.hrm_salary.core.domain.common.Utils;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.example.hrm_salary.core.domain.entity.SalaryTemplatesEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Objects;
@Component
public class SpecificationSalaryTemplate implements Specification<SalaryTemplatesEntity> {
    private SpecificationSalaryTemplate() {
    }
    public static Specification<SalaryTemplatesEntity> filterById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
    public static Specification<SalaryTemplatesEntity> filterIds(Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Expression<Long> warehouseIdExpression = root.get("id");
            Predicate predicate = warehouseIdExpression.in(ids);
            return criteriaBuilder.and(predicate);
        };
    }

    public static Specification<SalaryTemplatesEntity> filterNameOrCode(String search) {
        if (Objects.isNull(search) || search.trim().length() == 0) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
        {
            Predicate predicate = criteriaBuilder.conjunction();

            return criteriaBuilder.and(predicate, criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), Utils.buildSearch(search.toLowerCase())),
                    criteriaBuilder.or(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), Utils.buildSearch(search.toLowerCase()))))));
        };
    }

    public static Specification<SalaryTemplatesEntity> filterName(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), Utils.buildSearch(name.toLowerCase()));
    }

    @Override
    public Predicate toPredicate(Root<SalaryTemplatesEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }
}
