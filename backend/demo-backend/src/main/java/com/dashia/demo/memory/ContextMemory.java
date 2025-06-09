package com.dashia.demo.memory;

import java.util.*;

public class ContextMemory {

    private static final Map<Long, List<String>> memory = new HashMap<>();

    public static void addEntry(Long projectId, String prompt, String response) {
        memory.computeIfAbsent(projectId, k -> new ArrayList<>())
              .add("Prompt: " + prompt + "\nResposta: " + response);
    }

    public static String getContext(Long projectId) {
        return String.join("\n\n", memory.getOrDefault(projectId, new ArrayList<>()));
    }

    public static void clearMemory(Long projectId) {
        memory.remove(projectId);
    }
}
