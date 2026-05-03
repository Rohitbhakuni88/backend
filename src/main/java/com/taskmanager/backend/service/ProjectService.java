package com.taskmanager.backend.service;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 1. CREATE
    public Project createProject(Project project) {
        // Tip: You might also want to automatically set the project's user here
        // based on the SecurityContextHolder so the frontend doesn't have to send it.
        return projectRepository.save(project);
    }

    // 2. READ ALL
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // 3. READ ONE
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    // 4. DELETE (Secured against IDOR)
    public void deleteProject(Long id) {
        // Step 1: Fetch the project from the database
        Project project = getProjectById(id);

        // Step 2: Extract the currently authenticated user from the JWT context
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Step 3: Verify Ownership
        // IMPORTANT: Ensure your Project entity has a relationship mapped to the User entity.
        // Change '.getUser().getEmail()' if your fields are named differently (e.g., getOwner(), getUsername()).
        if (project.getUser() == null || !project.getUser().getEmail().equals(currentUsername)) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this project");
        }

        // Step 4: Execute the deletion only after passing the security check
        projectRepository.delete(project);
    }
}