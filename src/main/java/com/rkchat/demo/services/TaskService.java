package com.rkchat.demo.services;

import com.rkchat.demo.dto.CreateTaskRequest;
import com.rkchat.demo.dto.TaskDTO;
import com.rkchat.demo.enums.TaskStatus;
import com.rkchat.demo.models.Task;
import com.rkchat.demo.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TaskService {


    private TaskRepository taskRepository;


    private MessageService messageService; // Your existing service

    public TaskService(TaskRepository taskRepository, MessageService messageService) {
        this.taskRepository = taskRepository;
        this.messageService = messageService;
    }


    public TaskDTO createTaskFromMessage(CreateTaskRequest request, Long userId){
        System.out.println("=== TASK CREATION DEBUG ===");
        System.out.println("Requested messageId: " + request.getMessageId());
        System.out.println("Current userId: " + userId);

        if (!messageService.userCanAccessMessage(request.getMessageId(),userId)){
            throw new RuntimeException("User doesn't have access to this message");
        }
        Task task = new Task(userId,request.getMessageId(), request.getTitle());
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }
    public Page<TaskDTO> getUserTasks(Long userId, TaskStatus status, Pageable pageable) {
        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByUserIdAndStatusOrderByDueDateAsc(userId, status, pageable);
        } else {
            tasks = taskRepository.findByUserIdOrderByDueDateAsc(userId, pageable);
        }
        return tasks.map(this::convertToDTO);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long taskId, TaskStatus status, Long userId){
        Task task = taskRepository.findByIdAndUserId(taskId,userId )
        .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setUserId(task.getUserId());
        dto.setMessageId(task.getMessageId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }

}
