package org.example.hrm_salary.app.api;

import io.swagger.v3.oas.annotations.Operation;
import org.example.hrm_salary.app.dto.request.SalaryTemplateRequest.SalaryTemplateRequest;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateDetailResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateResponse;
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

public interface SalaryTemplateApi {
    @GetMapping("/list")
    @Operation(summary = "Get list SalaryTemplate ")
    Page<SalaryTemplateResponse> getSalaryTemplatePage(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @ParameterObject  Pageable pageable);


    @GetMapping("/detail")
    @Operation(summary = "Get detail SalaryTemplate ")
    SalaryTemplateDetailResponse getSalaryTemplateById(@RequestParam Long templateId);

    @PostMapping("/create")
    @Operation(summary = "Create new SalaryTemplate")
    IdResponse createSalaryTemplate(@RequestBody @Valid SalaryTemplateRequest salaryTemplateRequest);

    @PostMapping("/delete")
    @Operation(summary = "Delete SalaryTemplate")
    void deleteSalaryTemplate( @RequestParam Long salaryTemplateId);

    @PostMapping("/update")
    @Operation(summary = "Update SalaryTemplate")
    IdResponse updateSalaryTemplate( @RequestParam Long salaryTemplateId,
                                     @RequestBody @Valid SalaryTemplateRequest salaryTemplateRequest);
}
