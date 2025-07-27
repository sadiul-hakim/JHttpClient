package xyz.sadiulhakim.process;

import java.util.List;

public class ProcessAccessor {
    private ProcessAccessor() {
    }

    private static final List<String> CLEAR_COMMAND_ON_WINDOWS = List.of("cmd", "/c", "cls");
    private static final String SYSTEM = System.getProperty("os.name").toLowerCase();
    private static final List<String> CLEAR_COMMAND_ON_OTHERS = List.of("clear");


    public static void clear() {

        try {
            if (SYSTEM.contains("win")) {
                new ProcessBuilder(CLEAR_COMMAND_ON_WINDOWS).inheritIO().start().waitFor();
            } else {
                new ProcessBuilder(CLEAR_COMMAND_ON_OTHERS).inheritIO().start().waitFor();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
