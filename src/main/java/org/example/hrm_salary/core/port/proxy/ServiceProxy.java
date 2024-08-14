package org.example.hrm_salary.core.port.proxy;

import org.example.hrm_salary.app.dto.response.department.DataDepartmentResponse;
import org.example.hrm_salary.app.dto.response.department.DepartmentResponse;
import org.example.hrm_salary.app.dto.response.employee.DataEmployeeResponse;
import org.example.hrm_salary.app.dto.response.employee.EmployeeResponse;

import java.util.List;
import java.util.Map;

public interface ServiceProxy {

    public Map<Long, DataEmployeeResponse> getEmployeesByListIdsMap(List<Long> employeeIds);

    public Map<Long, DataDepartmentResponse> getDepartmentsByListIdsMap(List<Long> departmentIds);
}
