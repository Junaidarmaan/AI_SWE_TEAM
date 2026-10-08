package com.junnu.devflow.services;

import com.junnu.devflow.dto.AgentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AgentEventService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {

        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        return emitter;
    }

    public void sendEvent(
            String agent,
            String type,
            String message) {

        AgentEvent event = new AgentEvent(
                agent,
                type,
                message,
                Instant.now()
        );

        for (SseEmitter emitter : emitters) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("agent-event")
                                .data(event)
                );

            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}