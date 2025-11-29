package com.example.trabalho_biblioteca.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final RestTemplate restTemplate;

    public AiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> body) {
        // body: { "messages": [ { "role": "...", "content": "..." }, ... ] }
        List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");

        String prompt = "";
        if (messages != null && !messages.isEmpty()) {
            Map<String, String> last = messages.get(messages.size() - 1);
            prompt = last.getOrDefault("content", "");
        }

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", prompt);
        request.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "http://localhost:11434/api/generate",
                entity,
                JsonNode.class
        );

        JsonNode json = response.getBody();
        if (json == null || !json.has("response")) {
            return Map.of("reply", "Não foi possível obter resposta da IA local.");
        }

        String reply = json.get("response").asText();
        return Map.of("reply", reply);
    }
}
