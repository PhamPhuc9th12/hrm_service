package org.example.hrm_salary.core.port.proxy;

import org.example.hrm_salary.app.dto.response.department.DepartmentResponse;
import org.example.hrm_salary.app.dto.response.employee.EmployeeResponse;

import java.util.List;

public interface ServiceProxy {

    EmployeeResponse getEmployeesByListIds(List<Long> employeeIds);

    DepartmentResponse getDepartmentsByListIds(List<Long> departmentIds);
}
