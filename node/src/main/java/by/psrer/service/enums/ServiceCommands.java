package by.psrer.service.enums;

public enum ServiceCommands {
    START("/start"),
    HELP("/help");

    private final String cmd;

    ServiceCommands(final String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public Boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }
}
