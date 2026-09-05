package com.junnu.devflow.services;

import org.springframework.stereotype.Service;

import com.junnu.devflow.ai.assistants.BusinessAnalyst;

@Service 
public class BusinessAnalystService {
    BusinessAnalyst agent;
    public BusinessAnalystService(BusinessAnalyst agent) {
        this.agent = agent;
    }
    public String chat(String msg){
        return agent.chat(msg);
    }
    
}
