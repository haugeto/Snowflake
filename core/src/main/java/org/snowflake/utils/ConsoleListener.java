package org.snowflake.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Handles input from the developer console
 * 
 * @author haugeto
 */
public class ConsoleListener implements Runnable {

    enum InputToken {
        DUMP("Show previously generated scaffold"), SAVE("Save previously generated scaffold to file"), HELP(
                "Show help screen"), EXIT("Exit snowflake");

        String description;

        InputToken(String description) {
            this.description = description;
        }
    };

    File scaffoldSaveDir;

    public ConsoleListener() {
        this.scaffoldSaveDir = new File(System.getProperty("user.home"));
    }

    public ConsoleListener(File scaffoldSaveDir) {
        this.scaffoldSaveDir = scaffoldSaveDir;
    }

    boolean handleConsoleInput(String request) {
        boolean abort = false;
        InputToken inputToken;
        try {
            inputToken = InputToken.valueOf(request.toUpperCase());
        } catch (IllegalArgumentException e) {
            Console.println("Unknown command");
            return abort;
        }

        switch (inputToken) {
        case DUMP:
            Object previouslyGeneratedScaffoldName = Console.variables.get("previouslyGeneratedScaffold.name");
            Object previouslyGeneratedScaffoldContent = Console.variables.get("previouslyGeneratedScaffold.content");
            String name;
            if (previouslyGeneratedScaffoldContent != null && previouslyGeneratedScaffoldName != null) {
                name = previouslyGeneratedScaffoldName.toString();
                Console.println("--- " + name + " ---");
                Console.println(previouslyGeneratedScaffoldContent.toString());
            } else {
                Console.println("No scaffold has been generated");
            }
            break;
        case SAVE:
            previouslyGeneratedScaffoldName = Console.variables.get("previouslyGeneratedScaffold.name");
            previouslyGeneratedScaffoldContent = Console.variables.get("previouslyGeneratedScaffold.content");
            if (previouslyGeneratedScaffoldContent != null && previouslyGeneratedScaffoldName != null) {
                name = previouslyGeneratedScaffoldName.toString();
                String content = previouslyGeneratedScaffoldContent.toString();
                
                File f = new File(this.scaffoldSaveDir, name);
                
                Console.println("Saving " + content.length() + " bytes to \"" + f.getAbsolutePath() + "\"");
                try {
                    FileWriter writer = new FileWriter(f);
                    StreamHelpers.pipeToStream(new StringReader(content), writer);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                   Console.println("Couldn't save file: " + e.getMessage()); 
                }
                
            } else {
                Console.println("No scaffold has been generated");
            }
            
            
            break;

        case EXIT:
            Console.println("Good night :-)");
            abort = true;
            break;
        case HELP:
            Console.println("Usage:");
            Console.hr();
            for (InputToken token : InputToken.values()) {
                Console.justify("    \"" + token.name().toLowerCase() + "\"", token.description, '.');
            }
            break;
        }
        return abort;
    }

    public void run() {
        boolean abort = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!abort) {
            try {
                String request = br.readLine();
                abort = handleConsoleInput(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}