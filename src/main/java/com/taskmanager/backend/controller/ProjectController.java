package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 1. CREATE: Accessible by any authenticated user (Admin or Member)
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            projectService.createProject(project);
            return ResponseEntity.ok("Project created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Backend Error: " + e.getMessage());
        }
    }

    // 2. READ ALL: Lists all projects for the Dashboard
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    // 3. READ ONE: Fetches a specific project for the View Tasks page
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Project not found with id: " + id);
        }
    }

    // 4. DELETE: Strictly restricted to the ADMIN role
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Access Denied: Only Admins can delete projects");
        }
    }
}