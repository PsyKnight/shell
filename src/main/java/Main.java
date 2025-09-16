import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("$ ");

            String input = scanner.nextLine().trim();

            String[] commands = input.split(" ");

            switch (commands[0]) {
                case "echo" -> {
                    if (input.split(" ").length < 2) {
                        System.out.println();
                    } else {
                        echo(input.split(" ")[1]);
                    }
                }

                case "type" -> getType(commands[1]);

                case "exit" -> exit();

                default -> {
                    System.out.printf("%s: command not found%n", input);
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
        } else if (isExecutable(input) != null) {
            System.out.println(isExecutable(input));
        } else {
            System.out.println(input + ": not found");
        }
    }

    public static String isExecutable(String fileName) {
        String[] pathCommands = System.getenv("PATH").split(File.pathSeparator);

        for (String path : pathCommands) {
            File file = new File(path, fileName);

            if (file.exists()) {
                if (file.canExecute())
                    return fileName + " is " + file.getAbsolutePath();
            }
        }

        return null;
    }

    public static boolean isInbuiltCommand(String input) {
        Set<String> inbuilt = Set.of("exit", "echo", "type");

        return inbuilt.contains(input);
    }
}