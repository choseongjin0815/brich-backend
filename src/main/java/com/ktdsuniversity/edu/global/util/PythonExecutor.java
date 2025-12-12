package com.ktdsuniversity.edu.global.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PythonExecutor {

    public static String runPython(String scriptPath, String... args) {
        if (scriptPath == null || scriptPath.isBlank()) {
            throw new IllegalArgumentException("Script path cannot be null or empty");
        }

        String pythonExec = "C:\\Python314\\python.exe"; // adjust if needed
        List<String> command = new ArrayList<>();
        command.add(pythonExec);
        command.add(scriptPath);

        if (args != null) {
            for (String arg : args) {
                if (arg != null && !arg.isBlank()) {
                    command.add(arg);
                }
            }
        }

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            System.out.println("Executing: " + String.join(" ", command));

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON] " + line);
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script exited with code " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
