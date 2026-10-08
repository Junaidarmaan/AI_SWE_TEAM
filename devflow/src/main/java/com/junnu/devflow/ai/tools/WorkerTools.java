package com.junnu.devflow.ai.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import com.junnu.devflow.services.AgentEventService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WorkerTools {

    private final Path projectDirectory;
    private final Path planDirectory;
    private final AgentEventService eventService;

    public WorkerTools(AgentEventService eventService) {

        Path environmentDirectory =
                Path.of("agent_environment");

        this.projectDirectory =
                environmentDirectory.resolve("project");

        this.planDirectory =
                environmentDirectory.resolve("plan");

        this.eventService = eventService;
    }

    // ============================================================
    // PLAN TOOLS
    // ============================================================

    @Tool("List all worker plan files available in the agent_environment/plan directory")
    public String listPlanFiles() {

        try (Stream<Path> paths = Files.list(planDirectory)) {

            List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());

            if (files.isEmpty()) {

                eventService.sendEvent(
                        "Worker 1",
                        "activity",
                        "Checking the worker plans. No plans were found."
                );

                return "No plan files found.";
            }

            eventService.sendEvent(
                    "Worker 1",
                    "activity",
                    "Reviewed the available worker plans."
            );

            return String.join("\n", files);

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to read the worker plan directory."
            );

            return "Failed to list plan files: " + e.getMessage();
        }
    }


    @Tool("Read a worker plan file from agent_environment/plan. Provide only the file name.")
    public String readPlanFile(String fileName) {

        try {

            Path planFile =
                    resolveSafePath(planDirectory, fileName);

            if (!Files.exists(planFile)) {

                eventService.sendEvent(
                        "Worker 1",
                        "error",
                        "Worker plan not found: " + fileName
                );

                return "Plan file does not exist: " + fileName;
            }

            if (!Files.isRegularFile(planFile)) {
                return "The specified plan path is not a file: " + fileName;
            }

            String content =
                    Files.readString(
                            planFile,
                            StandardCharsets.UTF_8
                    );

            eventService.sendEvent(
                    "Worker 1",
                    "planning",
                    "Reading implementation plan: " + fileName
            );

            return content;

        } catch (IllegalArgumentException e) {

            return "Invalid plan file path: " + e.getMessage();

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to read worker plan: " + fileName
            );

            return "Failed to read plan file: " + e.getMessage();
        }
    }


    // ============================================================
    // PROJECT TOOLS
    // ============================================================

    @Tool("List all files currently present in the agent_environment/project directory")
    public String listFiles() {

        try (Stream<Path> paths =
                     Files.walk(projectDirectory)) {

            List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(projectDirectory::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());

            if (files.isEmpty()) {

                eventService.sendEvent(
                        "Worker 1",
                        "activity",
                        "Checked the project workspace. It is currently empty."
                );

                return "Project directory is empty.";
            }

            eventService.sendEvent(
                    "Worker 1",
                    "activity",
                    "Inspected the existing project files."
            );

            return String.join("\n", files);

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to inspect the project workspace."
            );

            return "Failed to list project files: "
                    + e.getMessage();
        }
    }


    @Tool("Read an existing project file from agent_environment/project. Provide the relative file path.")
    public String readFile(String fileName) {

        try {

            Path file =
                    resolveSafePath(
                            projectDirectory,
                            fileName
                    );

            if (!Files.exists(file)) {
                return "File does not exist: " + fileName;
            }

            if (!Files.isRegularFile(file)) {
                return "The specified path is not a file: "
                        + fileName;
            }

            String content =
                    Files.readString(
                            file,
                            StandardCharsets.UTF_8
                    );

            eventService.sendEvent(
                    "Worker 1",
                    "activity",
                    "Inspected project file: " + fileName
            );

            return content;

        } catch (IllegalArgumentException e) {

            return "Invalid project file path: "
                    + e.getMessage();

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to read project file: "
                            + fileName
            );

            return "Failed to read file: "
                    + e.getMessage();
        }
    }


    @Tool("Create a new project file inside agent_environment/project. The file must not already exist.")
    public String createFile(
            String fileName,
            String content) {

        try {

            Path file =
                    resolveSafePath(
                            projectDirectory,
                            fileName
                    );

            if (Files.exists(file)) {

                return "File already exists: "
                        + fileName
                        + ". Use updateFile instead.";
            }

            Path parent =
                    file.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    file,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW
            );

            eventService.sendEvent(
                    "Worker 1",
                    "file",
                    "Created " + fileName
            );

            return "Successfully created file: "
                    + fileName;

        } catch (IllegalArgumentException e) {

            return "Invalid project file path: "
                    + e.getMessage();

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to create " + fileName
            );

            return "Failed to create file: "
                    + e.getMessage();
        }
    }


    @Tool("Replace the entire contents of an existing project file inside agent_environment/project.")
    public String updateFile(
            String fileName,
            String content) {

        try {

            Path file =
                    resolveSafePath(
                            projectDirectory,
                            fileName
                    );

            if (!Files.exists(file)) {

                return "File does not exist: "
                        + fileName
                        + ". Use createFile instead.";
            }

            if (!Files.isRegularFile(file)) {

                return "The specified path is not a file: "
                        + fileName;
            }

            Files.writeString(
                    file,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            eventService.sendEvent(
                    "Worker 1",
                    "file",
                    "Updated " + fileName
            );

            return "Successfully updated file: "
                    + fileName;

        } catch (IllegalArgumentException e) {

            return "Invalid project file path: "
                    + e.getMessage();

        } catch (IOException e) {

            eventService.sendEvent(
                    "Worker 1",
                    "error",
                    "Failed to update " + fileName
            );

            return "Failed to update file: "
                    + e.getMessage();
        }
    }


    // ============================================================
    // PATH SAFETY
    // ============================================================

    /**
     * Resolves a user/agent-provided relative path and makes sure
     * it stays inside the intended directory.
     */
    private Path resolveSafePath(
            Path baseDirectory,
            String fileName) {

        if (fileName == null || fileName.isBlank()) {

            throw new IllegalArgumentException(
                    "File name cannot be empty."
            );
        }

        Path resolved =
                baseDirectory
                        .resolve(fileName)
                        .normalize();

        Path normalizedBase =
                baseDirectory.normalize();

        if (!resolved.startsWith(normalizedBase)) {

            throw new IllegalArgumentException(
                    "Path must remain inside the allowed directory."
            );
        }

        return resolved;
    }
}