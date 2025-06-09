package com.dashia.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.dashia.demo.model.Project;
import com.dashia.demo.model.PromptRequest;
import com.dashia.demo.repository.ProjectRepository;
import com.dashia.demo.service.IAService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectRepository repository;
    
    @Autowired
    private IAService iaService;

    @PostMapping("/{projectId}/chat")
    public String chatWithIA(
        @PathVariable Long projectId,
        @RequestBody PromptRequest dto
    ) {
        return iaService.generate(projectId, dto.getPrompt(), dto.getLanguage());
    }

    // ✅ Listar todos os projetos
    @GetMapping
    public List<Project> getAllProjects() {
        return repository.findAll();
    }

    // ✅ Buscar projeto por ID
    @GetMapping("/{id}")
    public Optional<Project> getProjectById(@PathVariable Long id) {
        return repository.findById(id);
    }

    // ✅ Buscar por nome (parcial)
    @GetMapping("/search")
    public List<Project> searchByName(@RequestParam String name) {
        return repository.findByNameContaining(name);
    }

    // ✅ Buscar por linguagem
    @GetMapping("/language/{language}")
    public List<Project> getByLanguage(@PathVariable String language) {
        return repository.findByLanguage(language);
    }

    // ✅ Criar um novo projeto
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return repository.save(project);
    }

    // ✅ Atualizar um projeto existente
    @PutMapping("/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody Project project) {
        Project existing = repository.findById(id).orElseThrow();
        existing.setName(project.getName());
        existing.setPrompt(project.getPrompt());
        existing.setLanguage(project.getLanguage());
        existing.setGeneratedCode(project.getGeneratedCode());
        return repository.save(existing);
    }

    // ✅ Deletar um projeto
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
