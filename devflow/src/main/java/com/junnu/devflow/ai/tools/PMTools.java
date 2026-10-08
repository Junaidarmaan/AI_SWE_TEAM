package com.junnu.devflow.ai.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.junnu.devflow.services.AgentEventService;
import com.junnu.devflow.services.WorkerAgentService;

import dev.langchain4j.agent.tool.Tool;

@Component
public class PMTools {

    private final WorkerAgentService worker;
    private final AgentEventService eventService;

    public PMTools(
            WorkerAgentService worker,
            AgentEventService eventService) {

        this.worker = worker;
        this.eventService = eventService;
    }

    private final String requirementsPath = "agent_environment/requirements";
    private final String planPath = "agent_environment/plan";


    @Tool("List all files available in the requirements folder")
    public String listRequirementFiles() {

        try {

            Path directory = Paths.get(requirementsPath);

            if (!Files.exists(directory)) {
                return "";
            }

            try (var files = Files.list(directory)) {

                String result = files
                        .filter(Files::isRegularFile)
                        .map(file -> file.getFileName().toString())
                        .collect(Collectors.joining("\n"));

                eventService.sendEvent(
                        "Project Manager",
                        "activity",
                        "Reviewed the available requirement documents."
                );

                return result;
            }

        } catch (IOException e) {

            e.printStackTrace();

            eventService.sendEvent(
                    "Project Manager",
                    "error",
                    "Failed to read the requirements workspace."
            );

            throw new RuntimeException(
                    "Failed to list requirement files", e);
        }
    }


    @Tool("Read the contents of a specific file from the requirements folder")
    public String readRequirementFile(String fileName) {

        try {

            Path filePath =
                    Paths.get(requirementsPath, fileName);

            if (!Files.exists(filePath)) {

                eventService.sendEvent(
                        "Project Manager",
                        "error",
                        "Requirement file not found: " + fileName
                );

                return "File not found: " + fileName;
            }

            String content =
                    Files.readString(filePath);

            eventService.sendEvent(
                    "Project Manager",
                    "analysis",
                    "Analyzing requirement document: " + fileName
            );

            return content;

        } catch (IOException e) {

            e.printStackTrace();

            eventService.sendEvent(
                    "Project Manager",
                    "error",
                    "Failed to read requirement document: " + fileName
            );

            throw new RuntimeException(
                    "Failed to read requirement file: " + fileName, e);
        }
    }


    @Tool("Create a planning file in the plan folder with the given file name and contents")
    public void createPlanFile(
            String fileName,
            String content) {

        try {

            Path filePath =
                    Paths.get(planPath, fileName);

            Files.createDirectories(
                    filePath.getParent());

            Files.writeString(
                    filePath,
                    content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println(
                    "Saved plan file to: "
                    + filePath.toAbsolutePath());

            eventService.sendEvent(
                    "Project Manager",
                    "plan",
                    "Created worker plan: " + fileName
            );

        } catch (IOException e) {

            e.printStackTrace();

            eventService.sendEvent(
                    "Project Manager",
                    "error",
                    "Failed to create worker plan: " + fileName
            );

            throw new RuntimeException(
                    "Failed to create plan file", e);
        }
    }


    @Tool("List the names of all available worker agents")
    public String listWorkers() {

        eventService.sendEvent(
                "Project Manager",
                "activity",
                "Checking the available worker agents."
        );

        return "worker1";
    }


    @Tool("Trigger all assigned agents to start working on their tasks")
    public void startAgents() {

        eventService.sendEvent(
                "Project Manager",
                "handoff",
                "Planning is complete. Starting the assigned worker agents."
        );

        System.out.println(
                "Agents triggered to start working.");

        worker.chat("start");

        System.out.println(
                "Agents done work.");

        eventService.sendEvent(
                "Project Manager",
                "complete",
                "Worker agents have completed their assigned tasks."
        );
    }


    public String resetEnvironment() {

        eventService.sendEvent(
                "System",
                "reset",
                "Resetting the agent environment."
        );

        String[] directories = {
                requirementsPath,
                planPath,
                "agent_environment/project"
        };

        try {

            for (String directoryPath : directories) {

                Path directory =
                        Paths.get(directoryPath);

                if (!Files.exists(directory)) {
                    continue;
                }

                try (var paths = Files.walk(directory)) {

                    paths
                            .sorted(java.util.Comparator.reverseOrder())
                            .filter(path -> !path.equals(directory))
                            .forEach(path -> {

                                try {
                                    Files.delete(path);

                                } catch (IOException e) {

                                    throw new RuntimeException(
                                            "Failed to delete: " + path,
                                            e);
                                }
                            });
                }
            }

            eventService.sendEvent(
                    "System",
                    "reset",
                    "Agent environment reset successfully."
            );

            return "Agent environment reset successfully. "
                    + "Requirements, plan, and project folders are now empty.";

        } catch (IOException | RuntimeException e) {

            eventService.sendEvent(
                    "System",
                    "error",
                    "Failed to reset the agent environment."
            );

            throw new RuntimeException(
                    "Failed to reset agent environment", e);
        }
    }
}