import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;


public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("$ ");

            String input = scanner.nextLine().trim();

            String[] commands = input.split("\\s+");

            switch (commands[0]) {
                case "echo" -> {
                    if (input.split("\\s+").length < 2) {
                        System.out.println();
                    } else {
                        echo(input.split("\\s+")[1]);
                    }
                }

                case "type" -> getType(commands[1]);

                case "exit" -> exit();

                case "test" -> {
                    String[] pathCommands = System.getenv("PATH").split(File.pathSeparator);
                    System.out.println(Arrays.toString(pathCommands));
                }

                default -> {
                    // Validation of command is done inside try-catch in executeFile
                    executeFile(commands);
                }
            }
        } while (true);
    }

    public static void echo(String input) {
        System.out.println(input);
    }

    public static void exit() {
        System.exit(0);
    }

    public static void getType(String input) {
        if (isInbuiltCommand(input)) {
            System.out.println(input + " is a shell builtin");
        } else if (getFilePath(input) != null) {
            System.out.println(input + " is " + getFilePath(input));
        } else {
            System.out.println(input + ": not found");
        }
    }

    public static void executeFile(String[] commands) throws IOException, InterruptedException {
        String filePath = getFilePath(commands[0]);

        try {

            ProcessBuilder builder = new ProcessBuilder(commands);

            // By default, ProcessBuilder has two default streams: standard output and standard error
            // This tells it to merge std error to std output stream
            builder.redirectErrorStream(true);

            Process process = builder.start();

            process.getInputStream().transferTo(System.out);

            process.waitFor();

        } catch (IOException e) {
            // In case of invalid command like "AsdasDasd"
            System.out.println(commands[0] + ": command not found");
        }
    }

    public static String getFilePath(String fileName) {
        String[] pathCommands = System.getenv("PATH").split(File.pathSeparator);

        for (String path : pathCommands) {
            File file = new File(path, fileName);

            if (file.exists()) {
                if (file.canExecute())
                    return file.getAbsolutePath();
            }
        }

        return null;
    }

    public static boolean isInbuiltCommand(String input) {
        Set<String> inbuilt = Set.of("exit", "echo", "type");

        return inbuilt.contains(input);
    }
}