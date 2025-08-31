package com.rkchat.demo.dto;

import com.rkchat.demo.enums.TaskPriority;
import com.rkchat.demo.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Long userId;
    private Long messageId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;


    // Optional: Add these for better frontend experience
    private String messageContent;
    private String senderName;
    private boolean isOverdue;
    private String timeRemaining;   // "2 hours left", "3 days left"

    public TaskDTO() {}

    // Helper method to check if task is overdue
    public boolean isOverdue() {
        return dueDate != null &&
                dueDate.isBefore(LocalDateTime.now()) &&
                status == TaskStatus.PENDING;
    }
}
