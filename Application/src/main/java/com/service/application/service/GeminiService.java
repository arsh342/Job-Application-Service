package com.service.application.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private static final String API_KEY = "AIzaSyDKDMdDPzcTQBa-RK_yA7RtP6-AnNmkaZY";
    private final Client client;

    public GeminiService() {
        this.client = Client.builder().apiKey(API_KEY).build();
    }

    public String summarizeJob(String jobDescription) {
        String prompt = "Summarize this job in 2-3 sentences for a candidate: " + jobDescription;
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );
        return response.text();
    }
}
