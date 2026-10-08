package com.junnu.devflow.services;

import org.springframework.stereotype.Service;

import com.junnu.devflow.ai.assistants.ProjectManager;

@Service 
public class ProjectManagerService {
    ProjectManager agent;
    public ProjectManagerService(ProjectManager agent){
        this.agent = agent;
    }
    public void trigger(){
        agent.chat("start");
    }
}
