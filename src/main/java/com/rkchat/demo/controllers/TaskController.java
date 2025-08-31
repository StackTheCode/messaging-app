package com.rkchat.demo.controllers;


import com.rkchat.demo.dto.CreateTaskRequest;
import com.rkchat.demo.dto.TaskDTO;
import com.rkchat.demo.dto.UpdateTaskStatusRequest;
import com.rkchat.demo.enums.TaskStatus;


import com.rkchat.demo.services.CustomUserDetailsService;
import com.rkchat.demo.services.TaskService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final CustomUserDetailsService customUserDetailsService;
    private TaskService taskService;

    public TaskController(TaskService taskService, CustomUserDetailsService customUserDetailsService) {
        this.taskService = taskService;
        this.customUserDetailsService = customUserDetailsService; // Add this
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequest request, Authentication auth){
        Long userId = getCurrentUserId(auth);
        TaskDTO task = taskService.createTaskFromMessage(request, userId);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            Pageable pageable,
            Authentication auth){
        Long userId = getCurrentUserId(auth);
        Page<TaskDTO> tasks = taskService.getUserTasks(userId, status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        TaskDTO updatedTask = taskService.updateTaskStatus(taskId, request.getStatus(), userId);
        return ResponseEntity.ok(updatedTask);
    }
@DeleteMapping("/{taskId}")
public ResponseEntity<Void> deleteTasK(@PathVariable Long taskId, Authentication auth){
        Long userId = getCurrentUserId(auth);
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
}


    private Long getCurrentUserId(Authentication auth) {
  String username = auth.getName();
  return customUserDetailsService.getUserIdByUsername(username);
    }

}
