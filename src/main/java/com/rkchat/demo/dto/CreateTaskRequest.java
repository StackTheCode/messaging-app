package com.rkchat.demo.dto;

import com.rkchat.demo.enums.TaskPriority;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;


@Getter
@Setter
public class CreateTaskRequest {

    private Long messageId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskPriority priority = TaskPriority.MEDIUM;

    public CreateTaskRequest() {}

    public CreateTaskRequest(Long messageId, String title) {
        this.messageId = messageId;
        this.title = title;
    }
}