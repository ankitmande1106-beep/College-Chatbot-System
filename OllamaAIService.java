package com.college.chatbot;

import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * AI Service that connects to a local Ollama instance running Llama3.
 * Used as a fallback when FAQ keyword matching doesn't find an answer.
 */
public class OllamaAIService {

    private static final OllamaAIService instance = new OllamaAIService();

    private OllamaChatModel model;
    private boolean available;

    private OllamaAIService() {
        try {
            this.model = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("llama3")
                    .timeout(java.time.Duration.ofSeconds(60))
                    .build();
            this.available = true;
            System.out.println("✅ Ollama AI Service initialized successfully.");
        } catch (Exception e) {
            this.available = false;
            System.err.println("⚠️ Ollama AI Service not available: " + e.getMessage());
        }
    }

    public static OllamaAIService getInstance() {
        return instance;
    }

    /**
     * Get an AI-generated response for the user's message.
     * If Ollama is not available, returns a friendly fallback message.
     */
    public String getResponse(String userMessage) {
        if (!available || model == null) {
            return "I'm sorry, I don't have an answer for that right now. "
                    + "Please contact the college office for more information.";
        }

        try {
            String prompt = "You are Atharva AI Assistant, a helpful college chatbot for Atharva College of Engineering. "
                    + "Answer the student's question concisely in 1-3 sentences. "
                    + "Be friendly and informative.\n\n"
                    + "Student: " + userMessage;

            String response = model.generate(prompt);
            return response;

        } catch (Exception e) {
            System.err.println("Ollama AI generation failed: " + e.getMessage());
            return "I'm sorry, I couldn't process that right now. "
                    + "Please try again or contact the college office.";
        }
    }
}
