package com.junnu.devflow.dto;

import java.time.Instant;

public class AgentEvent {

    private String agent;
    private String type;
    private String message;
    private Instant timestamp;

    public AgentEvent() {
    }

    public AgentEvent(
            String agent,
            String type,
            String message,
            Instant timestamp) {

        this.agent = agent;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}