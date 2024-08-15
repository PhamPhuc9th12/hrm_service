package org.example.hrm_salary.app.rest;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.app.api.SalaryTemplateApi;
import org.example.hrm_salary.app.dto.request.SalaryTemplateRequest.SalaryTemplateRequest;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateDetailResponse;
import org.example.hrm_salary.app.dto.response.SalaryTemplateResponse.SalaryTemplateResponse;
import org.example.hrm_salary.core.domain.constant.IdResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/salary-template")
@AllArgsConstructor
public class SalaryTemplateController implements SalaryTemplateApi {
    private final SalaryTemplateApi salaryTemplateService;

    @Override
    public SalaryTemplateDetailResponse getSalaryTemplateById(Long templateId){
        return salaryTemplateService.getSalaryTemplateById(templateId);
    }
    @Override
    public Page<SalaryTemplateResponse> getSalaryTemplatePage(Long id, String search, String name,Pageable pageable) {
        return salaryTemplateService.getSalaryTemplatePage(id,search,name,pageable);
    }

    @Override
    public void exportSalaryTemplateExcel(Long templateId)  {
        salaryTemplateService.exportSalaryTemplateExcel(templateId);
    }

    @Override
    public IdResponse createSalaryTemplate(SalaryTemplateRequest salaryTemplateRequest) {
        return salaryTemplateService.createSalaryTemplate(salaryTemplateRequest);
    }

    @Override
    public void deleteSalaryTemplate(Long salaryTemplateId) {
        salaryTemplateService.deleteSalaryTemplate(salaryTemplateId);
    }

    @Override
    public IdResponse updateSalaryTemplate(Long salaryTemplateId, SalaryTemplateRequest salaryTemplateRequest) {
        return salaryTemplateService.updateSalaryTemplate(salaryTemplateId,salaryTemplateRequest);
    }
}
