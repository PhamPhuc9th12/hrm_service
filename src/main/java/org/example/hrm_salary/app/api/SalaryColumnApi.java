package org.example.hrm_salary.app.api;

import io.swagger.v3.oas.annotations.Operation;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnsResponse;
import org.example.hrm_salary.core.domain.constant.IdResponse;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
public interface SalaryColumnApi {
    @PostMapping("/create")
    @Operation(summary = "Create new SalaryColumns")
    IdResponse createSalaryColumns(@RequestBody @Valid SalaryColumnRequest salaryColumnRequest);

    @PostMapping("/update")
    @Operation(summary = "Update SalaryColumns")
    IdResponse updateSalaryColumns(
            @RequestParam Long salaryColumnId,
            @RequestBody @Valid SalaryColumnRequest salaryColumnRequest);


    @PostMapping("/list")
    @Operation(summary = "get list salary columns ")
    Page<SalaryColumnsResponse> getSalaryColumns(@ParameterObject Pageable pageable);

    SalaryColumnsResponse getSalaryColumnsById(Long salaryColumnId);

    @PostMapping("/delete")
    @Operation(summary = "delete salary columns ")
    void deleteSalaryColumn(Long salaryColumnId);

    @GetMapping("/list-by-group")
    @Operation(summary = "Get list ColumnSalary by GroupColumnSalary Id")
    Page<SalaryColumnsResponse> getListSalaryColumnByGroupId(@RequestParam Long groupId,@ParameterObject Pageable pageable);
}
