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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public IdResponse exportSalaryTemplateExcel(Long templateId, HttpServletResponse response) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormat = now.format(formatter);
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=salary_template_"+dateFormat+".xlsx";
        response.setHeader(headerKey,headerValue);
        return salaryTemplateService.exportSalaryTemplateExcel(templateId,response);
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

//    @GetMapping("/export/{id}")
//    public ResponseEntity<byte[]> exportProductToExcel(@PathVariable Long id) throws IOException {
//        Product product = productService.getProductById(id); // Lấy thông tin sản phẩm theo ID
//        ByteArrayInputStream in = excelExportService.exportProductToExcel(product);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=product_" + id + ".xlsx");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(in.readAllBytes());
//    }
}
