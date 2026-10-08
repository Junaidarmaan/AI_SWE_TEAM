package com.junnu.devflow.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.junnu.devflow.services.AgentEventService;

@RestController
@RequestMapping("/api/agent-events")
public class AgentEventController {

    private final AgentEventService agentEventService;

    public AgentEventController(AgentEventService agentEventService) {
        this.agentEventService = agentEventService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        return agentEventService.subscribe();
    }
}