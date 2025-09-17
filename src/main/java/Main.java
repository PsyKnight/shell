import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");

            String inputString = scanner.nextLine().trim();

            String[] input = inputString.split("\\s+");

            String command = input[0];
            String[] arguments = Arrays.copyOfRange(input, 1, input.length);


            switch (command) {
                case "echo" -> echo(arguments);

                case "type" -> getType(arguments);

                case "exit" -> exit(arguments);

                case "pwd" -> printWorkingDirectory();

                case "cd" -> changeDirectory(arguments);

                default -> {
                    // Validation of command is done inside try-catch in executeFile
                    executeFile(input);
                }
            }
        }
    }

    public static void printWorkingDirectory() {
        System.out.println(System.getProperty("user.dir"));
    }

    public static void changeDirectory(String[] arguments) {
        if (arguments.length > 1) {
            System.out.println("cd: to many arguments");
            return;
        }

        String path = arguments[0];

        File dir = validateDirPath(path);

        if (dir != null) {
            System.setProperty("user.dir", path);
        } else {
            System.out.println("cd: " + path + " No such file or directory");
        }
    }

    public static File validateDirPath(String path) {
        File dir = new File(path);

        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        return null;
    }

    public static void echo(String[] arguments) {
        System.out.println(String.join(" ", arguments));
    }

    public static void exit(String[] arguments) {
        if (arguments.length > 1) {
            System.out.println("exit: too many arguments");
            return;
        }

        int statusCode = 0;

        if (arguments.length == 1) {
            String statusCodeString = arguments[0];

            try {

                statusCode = Byte.toUnsignedInt((byte) Integer.parseInt(statusCodeString));

            } catch (NumberFormatException e) {
                System.err.println("exit: " + statusCodeString + ": numeric argument required");
                return;
            }
        }

        System.exit(statusCode);
    }

    public static void getType(String[] arguments) {
        if (arguments.length > 1) {
            System.out.println("type: too many arguments");
            return;
        }

        String input = arguments[0];

        if (isInbuiltCommand(input)) {
            System.out.println(input + " is a shell builtin");
        } else if (getFilePath(input) != null) { // Check if it's a path command
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

            process.waitFor(); // Wait for the process to finish execution

        } catch (IOException e) {
            System.out.println(commands[0] + ": command not found");
        }
    }

    public static String getFilePath(String fileName) {
        String[] pathCommands = System.getenv("PATH").split(File.pathSeparator);

        for (String path : pathCommands) {
            File file = new File(path, fileName);

            if (file.exists() && file.canExecute()) {
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