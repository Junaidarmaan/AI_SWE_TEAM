package com.junnu.devflow.ai;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.junnu.devflow.ai.assistants.GeneralAssistant;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

@Configuration
public class AIConfig {
    @Bean
    GoogleAiGeminiChatModel initializeGeminiChatModel() {

        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                // .modelName("gemini-3.5-flash-lite")
                .modelName("gemini-3.1-flash-lite")

                // .modelName("gemma-4-31b-it")
                // .modelName("gemma-4-26b-it")

                .sendThinking(true)
                .returnThinking(true)
                .build();

        return model;
    }

    
    @Bean
    GeneralAssistant initializeIncidentAssistant(GoogleAiGeminiChatModel model) {
        return AiServices.builder(GeneralAssistant.class)
                .chatModel(model)
                .build();
    }

    
}

