package com.dashia.demo.model;

import jakarta.persistence.*;


@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; // Nome do projeto
    private String prompt;
    private String language;
      @Column(columnDefinition = "TEXT")
    private String generatedCode;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getGeneratedCode() { return generatedCode; }
    public void setGeneratedCode(String generatedCode) { this.generatedCode = generatedCode; }
}