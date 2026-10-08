package com.junnu.devflow.ai.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.junnu.devflow.services.AgentEventService;
import com.junnu.devflow.services.ProjectManagerService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class BATools {


    private final String path = "agent_environment/requirements";

    private final ProjectManagerService pm;
    private final AgentEventService eventService;
    private final PMTools pmTools;
    public BATools(
            ProjectManagerService pm,
            AgentEventService eventService,
            PMTools pmTools
        ) {

        this.pm = pm;
        this.eventService = eventService;
        this.pmTools = pmTools;
    }

    @Tool("triggers the project manager")
    public void triggerProjectManager() {

        eventService.sendEvent(
                "Business Analyst",
                "handoff",
                "Requirements are complete. Handing the project over to the Project Manager."
        );

        pm.trigger();
    }


    @Tool("Create a file in the agent environment folder with the given file name and write the provided content into it")
    public void makeReportFile(String description, String fileName) {

        try {

            Path filePath = Paths.get(path, fileName);

            Files.createDirectories(filePath.getParent());

            Files.writeString(
                    filePath,
                    description,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println(
                    "Saved file to: " + filePath.toAbsolutePath()
            );

            eventService.sendEvent(
                    "Business Analyst",
                    "artifact",
                    "Requirements document created: " + fileName
            );

        } catch (Exception e) {

            e.printStackTrace();

            eventService.sendEvent(
                    "Business Analyst",
                    "error",
                    "Failed to create the requirements document."
            );

            throw new RuntimeException("Failed to save file", e);
        }
    }


    @Tool("List the names of all files present in the agent environment folder")
    public String listFileNames() {

        try {

            Path directory = Paths.get(path);

            if (!Files.exists(directory)) {

                eventService.sendEvent(
                        "Business Analyst",
                        "activity",
                        "Checking the requirements workspace. No requirement files found."
                );

                return "";
            }

            String files = Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .collect(Collectors.joining("\n"));

            eventService.sendEvent(
                    "Business Analyst",
                    "activity",
                    "Reviewed the requirements workspace."
            );

            return files;

        } catch (IOException e) {

            e.printStackTrace();

            eventService.sendEvent(
                    "Business Analyst",
                    "error",
                    "Failed to read the requirements workspace."
            );

            throw new RuntimeException("Failed to list files", e);
        }
    }
    @Tool("used to reset environment")
    void resetEnvironment(){
        pmTools.resetEnvironment();
    }
    
}
