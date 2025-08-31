package com.rkchat.demo.repositories;

import com.rkchat.demo.enums.TaskStatus;
import com.rkchat.demo.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserIdOrderByDueDateAsc(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatusOrderByDueDateAsc(Long userId, TaskStatus status, Pageable pageable);
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.status = 'PENDING' AND t.dueDate <= :now")
    List<Task> findOverdueTasksForUser(@Param("userId") Long userId,@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN  :start  AND  :end AND t.status='PENDING' ")
    List<Task> findTasksDueBetween(@Param("start") LocalDateTime start, @Param("end" ) LocalDateTime end);
    @Query("SELECT  COUNT(t) FROM  Task t WHERE t.userId=: userId AND  t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userid, @Param("status") TaskStatus status);
}
