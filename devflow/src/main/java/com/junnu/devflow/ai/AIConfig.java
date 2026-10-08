package com.junnu.devflow.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.junnu.devflow.ai.assistants.BusinessAnalyst;
import com.junnu.devflow.ai.assistants.GeneralAssistant;
import com.junnu.devflow.ai.assistants.ProjectManager;
import com.junnu.devflow.ai.tools.BATools;
import com.junnu.devflow.ai.tools.PMTools;
import com.junnu.devflow.ai.tools.WorkerTools;
import com.junnu.devflow.ai.workers.worker1;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
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

    @Bean
    BusinessAnalyst initializeBusinessAnalyst(GoogleAiGeminiChatModel model, BATools baTools) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(25);
        return AiServices.builder(BusinessAnalyst.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(baTools)
                .build();
    }

    @Bean
    ProjectManager initializeProjectManager(GoogleAiGeminiChatModel model,PMTools pmTools) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(25);
        return AiServices.builder(ProjectManager.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(pmTools)
                .build();
    }

    @Bean
    worker1 worker1(GoogleAiGeminiChatModel model,WorkerTools tools) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(50);
        return AiServices.builder(worker1.class)
                .chatModel(model)
                .chatMemory(memory)
                .tools(tools)
                .build();
    }
}
