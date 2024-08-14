package org.example.hrm_salary.app.rest;

import lombok.AllArgsConstructor;
import org.example.hrm_salary.app.api.GroupSalaryColumnsApi;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnUpdateRequest;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnsCreateRequest;
import org.example.hrm_salary.app.dto.response.GroupSalaryColumnResponse.GroupSalaryColumnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/group-salary")
public class GroupSalaryColumnController implements GroupSalaryColumnsApi {

    private final GroupSalaryColumnsApi groupSalaryColumnService;

    @Override
    public void createGroupColumnsType( GroupSalaryColumnsCreateRequest groupSalaryColumnsCreateRequest) {
        groupSalaryColumnService.createGroupColumnsType(groupSalaryColumnsCreateRequest);
    }

    @Override
    public Page<GroupSalaryColumnResponse> getListGroupSalaryResponse(String name, String code, Pageable pageable) {
        return groupSalaryColumnService.getListGroupSalaryResponse(name,code,pageable);
    }

    @Override
    public void updateGroupColumnsType(GroupSalaryColumnUpdateRequest groupSalaryColumnUpdateRequest, Long columId) {
        groupSalaryColumnService.updateGroupColumnsType(groupSalaryColumnUpdateRequest,columId);
    }

    @Override
    public void deleteGroupColumnSalary(Long columnId) {
        groupSalaryColumnService.deleteGroupColumnSalary(columnId);
    }

    @Override
    public GroupSalaryColumnResponse getColumnById(Long columnId) {
        return groupSalaryColumnService.getColumnById(columnId);
    }


}
