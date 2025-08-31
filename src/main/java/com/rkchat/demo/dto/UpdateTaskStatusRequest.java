package com.rkchat.demo.dto;

import com.rkchat.demo.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskStatusRequest {
    private TaskStatus status;

    public UpdateTaskStatusRequest() {}

    public UpdateTaskStatusRequest(TaskStatus status) {
        this.status = status;
    }
}