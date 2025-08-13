package by.psrer.service.enums;

import lombok.Getter;

@Getter
public enum Command {
    START("/start"),
    HELP("/help"),
    FAQ("/faq"),
    ROUTES("/routes"),
    CANCEL("/cancel");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public Boolean equals(final String cmd) {
        return this.toString().equals(cmd);
    }

    public static Command fromString(String text) {
        for (Command cmd : Command.values()) {
            if (cmd.command.equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return null;
    }
}