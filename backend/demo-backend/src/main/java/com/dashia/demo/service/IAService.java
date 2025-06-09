package com.dashia.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dashia.demo.memory.ContextMemory;
import com.dashia.demo.model.Project;
import com.dashia.demo.repository.ProjectRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

@Service
public class IAService {

    @Autowired
    private ProjectRepository repository;

    public String generate(Long projectId, String prompt, String language) {
        String context = ContextMemory.getContext(projectId);

        // ✅ Novo prompt base mais flexível e inteligente
String fullPrompt = String.format(
    "Você é uma IA programadora especializada em Java, JavaScript e Python. " +
    "Seu papel é gerar sistemas completos, corrigir erros e explicar códigos nessas linguagens.\n\n" +

    "- Quando o usuário pedir um sistema completo, gere **todos os arquivos prontos**, com estrutura de pastas e camadas adequadas:\n" +
    "  - Em Java: use Model, Repository, Service, Controller (Spring Boot)\n" +
    "  - Em JavaScript: use rotas, controllers, models (Node.js com Express ou outro framework se citado)\n" +
    "  - Em Python: use classes, funções organizadas (Flask, Django ou puro, dependendo do pedido)\n\n" +

    "- Gere códigos reais e completos. Mostre tudo o que for necessário para rodar o sistema.\n" +
    "- Explique só se o usuário pedir. Priorize mostrar **o código funcional antes**.\n" +
    "- Se a linguagem não for especificada, use a linguagem '%s'.\n\n" +

    "- Se o usuário disser algo como 'oi', 'tudo bem?', 'boa tarde', 'obrigado', etc., responda de forma educada e breve, e convide-o a fazer uma pergunta sobre programação.\n\n" +

    "Contexto anterior:\n%s\n\n" +
    "Pedido atual:\n%s",
    language, context, prompt
);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "llama3"); // ou outro modelo local
            requestBody.put("prompt", fullPrompt);
            requestBody.put("stream", false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            JSONObject json = new JSONObject(responseBody);
            String generatedCode = json.getString("response");

            ContextMemory.addEntry(projectId, prompt, generatedCode);

            Project project = repository.findById(projectId).orElse(new Project());
            project.setPrompt(prompt);
            project.setLanguage(language);
            project.setGeneratedCode(generatedCode);
            repository.save(project);

            return new JSONObject().put("generatedCode", generatedCode).toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Erro ao gerar código: " + e.getMessage();
        }
    }
}
