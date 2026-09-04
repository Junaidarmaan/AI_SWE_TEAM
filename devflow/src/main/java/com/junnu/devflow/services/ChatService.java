package com.junnu.devflow.services;

import org.springframework.stereotype.Service;

import com.junnu.devflow.ai.assistants.GeneralAssistant;



@Service 
public class ChatService {
    GeneralAssistant assistant;
    public ChatService(GeneralAssistant assistant){
        this.assistant = assistant;
    }
    public String chat(String msg){
        return assistant.chat(msg);
    }

}
