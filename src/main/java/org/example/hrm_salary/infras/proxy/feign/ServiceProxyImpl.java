package org.example.hrm_salary.infras.proxy.feign;

import lombok.RequiredArgsConstructor;
import org.example.hrm_salary.app.dto.response.department.DepartmentResponse;
import org.example.hrm_salary.app.dto.response.department.FormatDepartmentContent;
import org.example.hrm_salary.app.dto.response.employee.EmployeeResponse;
import org.example.hrm_salary.core.domain.constant.ErrorCode;
import org.example.hrm_salary.core.port.proxy.ServiceProxy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ServiceProxyImpl implements ServiceProxy {
    private final ServiceClient serviceClient;
    @Override
    public EmployeeResponse getEmployeesByListIds(List<Long> employeeIds) {
        EmployeeResponse employeeResponses = serviceClient.getStaffByIds(employeeIds);
        if(Objects.isNull(employeeResponses)) throw new RuntimeException(ErrorCode.LIST_IS_EMPTY);
        return  employeeResponses;
    }

    @Override
    public DepartmentResponse getDepartmentsByListIds(List<Long> departmentIds) {
        FormatDepartmentContent formatDepartmentContent = serviceClient.getListDepartmentByIds(departmentIds);
        if(Objects.isNull(formatDepartmentContent)) throw new RuntimeException(ErrorCode.LIST_IS_EMPTY);
        return formatDepartmentContent.getData();
    }
}
