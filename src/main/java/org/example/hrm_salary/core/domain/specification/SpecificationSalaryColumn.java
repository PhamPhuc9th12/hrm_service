package org.example.hrm_salary.core.domain.specification;

import org.example.hrm_salary.core.domain.common.Utils;
import org.example.hrm_salary.core.domain.entity.SalaryColumnsEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Component
public class SpecificationSalaryColumn implements Specification<SalaryColumnsEntity> {
    private SpecificationSalaryColumn() {
    }
    public static Specification<SalaryColumnsEntity> filterById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
    public static Specification<SalaryColumnsEntity> filterIds(Collection<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Expression<Long> warehouseIdExpression = root.get("id");
            Predicate predicate = warehouseIdExpression.in(ids);
            return criteriaBuilder.and(predicate);
        };
    }

    public static Specification<SalaryColumnsEntity> filterNameOrCode(String search) {
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

    public static Specification<SalaryColumnsEntity> filterName(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), Utils.buildSearch(name.toLowerCase()));
    }

    @Override
    public Predicate toPredicate(Root<SalaryColumnsEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

//    public static Specification<SalaryColumnsEntity> withSourceProduct(StockWarehouseSourceProductType sourceProductType) {
//        if (Objects.isNull(sourceProductType)) {
//            return null;
//        }
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sourceProductType"),
//                sourceProductType));
//    }
//
//    public static Specification<SalaryColumnsEntity> withState(StockWarehouseStateType state) {
//        if (Objects.isNull(state)) {
//            return null;
//        }
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), state));
//    }
//
//    public static Specification<SalaryColumnsEntity> withStateIn(Set<StockWarehouseStateType> states) {
//        if (Objects.isNull(states) || states.isEmpty()) {
//            return null;
//        }
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states));
//    }
//
//
//    public static Specification<SalaryColumnsEntity> filterUserId(Long userId) {
//        if (Objects.isNull(userId)) {
//            return null;
//        }
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
//    }
//
//    public static Specification<SalaryColumnsEntity> filterCompanyId(Long companyId) {
//        if (Objects.isNull(companyId)) {
//            return null;
//        }
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("companyId"), companyId);
//    }
//
//    public static Specification<SalaryColumnsEntity> filterBranchId(Long branchId) {
//        if (Objects.isNull(branchId)) {
//            return null;
//        }
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("branchId"), branchId);
//    }
//
//    public static Specification<SalaryColumnsEntity> withNotEqualsStatusDraft(StockWarehouseStateType state) {
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("state"), state));
//    }
//
//    public static Specification<SalaryColumnsEntity> withWarehouseType(WarehouseType warehouseType) {
//        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("warehouseType"), warehouseType));
//    }

//    Specification<StockWarehouseEntity> conditions = Specification.where(StockWarehouseSpeciation.withNotEqualsStatusDraft(
//            StockWarehouseStateType.DRAFT));
//    conditions = conditions.and(StockWarehouseSpeciation.withWarehouseType(warehouseType));
//    conditions = conditions.and(StockWarehouseSpeciation.filterNameOrCode(search));
//    conditions = conditions.and(StockWarehouseSpeciation.filterName(name));
//    conditions = conditions.and(StockWarehouseSpeciation.filterCompanyId(companyId));
//    conditions = conditions.and(StockWarehouseSpeciation.filterBranchId(Helper.getBranchId()));
//    conditions = conditions.and(StockWarehouseSpeciation.withSourceProduct(sourceProductType));
//    conditions = conditions.and(StockWarehouseSpeciation.withState(state));
//    conditions = conditions.and(StockWarehouseSpeciation.filterIds(ids));
//
//        return warehouseRepository.findAll(conditions, pageable);
}