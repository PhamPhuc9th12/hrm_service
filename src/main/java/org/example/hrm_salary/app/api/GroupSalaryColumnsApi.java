package org.example.hrm_salary.app.api;

import io.swagger.v3.oas.annotations.Operation;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnUpdateRequest;
import org.example.hrm_salary.app.dto.request.GroupSalarayColumnRequest.GroupSalaryColumnsCreateRequest;
import org.example.hrm_salary.app.dto.response.GroupSalaryColumnResponse.GroupSalaryColumnResponse;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

public interface GroupSalaryColumnsApi {
    @PostMapping("/create")
    @Operation(summary = "Create new group salary column type ")
    void createGroupColumnsType(@Valid @RequestBody GroupSalaryColumnsCreateRequest groupSalaryColumnsCreateRequest);

    @GetMapping("/list")
    @Operation(summary = "Get list group salary column type ")
    Page<GroupSalaryColumnResponse> getListGroupSalaryResponse(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable);

    @PutMapping("/update")
    @Operation(summary = "Update group column type")
    void updateGroupColumnsType(@RequestBody @Valid GroupSalaryColumnUpdateRequest groupSalaryColumnUpdateRequest,
                                @RequestParam Long columId);

    @DeleteMapping("/delete")
    @Operation(summary = "Delete group salary column")
    void deleteGroupColumnSalary(@RequestParam Long columnId);

    @GetMapping("/column/id")
    @Operation(summary = "Get column salary by id")
    GroupSalaryColumnResponse getColumnById(@RequestParam Long columnId);
}
