package org.example.hrm_salary.app.rest;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.app.api.SalaryColumnApi;
import org.example.hrm_salary.app.dto.request.SalarayColumnRequest.SalaryColumnRequest;
import org.example.hrm_salary.app.dto.response.SalaryColumnResponse.SalaryColumnsResponse;
import org.example.hrm_salary.core.domain.constant.IdResponse;
import org.example.hrm_salary.core.service.SalaryColumnsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salary-column")
@AllArgsConstructor
public class SalaryColumnController implements SalaryColumnApi {
    private final SalaryColumnApi salaryColumnService;
    @Override
    public IdResponse createSalaryColumns(SalaryColumnRequest salaryColumnRequest) {
        return salaryColumnService.createSalaryColumns(salaryColumnRequest);
    }

    @Override
    public IdResponse updateSalaryColumns(Long salaryColumnId, SalaryColumnRequest salaryColumnRequest) {
        return salaryColumnService.updateSalaryColumns(salaryColumnId,salaryColumnRequest);
    }

    @Override
    public Page<SalaryColumnsResponse> getSalaryColumns(Long id, String search, String name,Pageable pageable) {
        return salaryColumnService.getSalaryColumns(id,search,name,pageable);
    }

    @Override
    public SalaryColumnsResponse getSalaryColumnsById(Long salaryColumnId) {
        return salaryColumnService.getSalaryColumnsById(salaryColumnId);
    }

    @Override
    public void deleteSalaryColumn(Long salaryColumnId) {
        salaryColumnService.deleteSalaryColumn(salaryColumnId);
    }

    @Override
    public Page<SalaryColumnsResponse> getListSalaryColumnByGroupId(Long groupId, Pageable pageable) {
        return salaryColumnService.getListSalaryColumnByGroupId(groupId,pageable);
    }
}
