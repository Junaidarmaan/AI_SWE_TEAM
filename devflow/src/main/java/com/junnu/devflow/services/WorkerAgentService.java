package com.junnu.devflow.services;

import org.springframework.stereotype.Service;

import com.junnu.devflow.ai.workers.worker1;

@Service
public class WorkerAgentService {
    worker1 agent;
    public WorkerAgentService(worker1 agent) {
        this.agent = agent;
    }
    public String chat(String msg){
        return agent.chat(msg);
    }
}
