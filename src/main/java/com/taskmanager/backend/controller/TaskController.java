package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.enums.TaskStatus;
import com.taskmanager.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 1. CREATE: Saves a task and returns a string to avoid JSON recursion crashes
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            taskService.createTask(task);
            return ResponseEntity.ok("Task created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Backend Error: " + e.getMessage());
        }
    }

    // 2. READ: Fetches all tasks for a specific project
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    // 3. UPDATE: Changes status (PENDING, IN_PROGRESS, COMPLETED)
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        try {
            // Convert String to Enum (e.g., "pending" -> TaskStatus.PENDING)
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());

            taskService.updateTaskStatus(taskId, taskStatus);
            return ResponseEntity.ok("Status updated to " + status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status. Use PENDING, IN_PROGRESS, or COMPLETED.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }

    // 4. DELETE: Removes a task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting task: " + e.getMessage());
        }
    }
}