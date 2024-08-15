package org.example.hrm_salary.infras.proxy.feign;

import org.example.hrm_salary.app.dto.response.department.FormatDepartmentContent;
import org.example.hrm_salary.app.dto.response.employee.EmployeeResponse;
import org.example.hrm_salary.app.dto.response.employee.FormatEmployeeContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

//@FeignClient("RESOURCES")
//@FeignClient(value = "https://resources-service.dev.apusplatform.com", url = "localhost:8086")
@FeignClient(value = "https://resources-service.stg.apusplatform.com", url = "https://resources-service.dev.apusplatform.com")
public interface ServiceClient {
    @GetMapping(value = "/api/v1/employee/by-ids", produces = "application/json")
    EmployeeResponse getStaffByIds(@RequestParam(required = false) List<Long> ids);

    @GetMapping(value =  "/api/v1/department/list-tiny", produces = "application/json")
    FormatDepartmentContent getListDepartmentByIds(@RequestParam(required = false) List<Long> ids);


    @GetMapping(value = "/api/v1/employee/list?size=500", produces = "application/json")
    FormatEmployeeContent getAllEmployee();
}
