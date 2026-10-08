package com.junnu.devflow.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

@Service 
public class CommandLine {
    public static String execute(String cmd) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("cmd", "/c", cmd)
                .redirectErrorStream(true)
                .start();

        String output = new String(
                process.getInputStream().readAllBytes());

        int exitCode = process.waitFor();

        return output;
    }
}
