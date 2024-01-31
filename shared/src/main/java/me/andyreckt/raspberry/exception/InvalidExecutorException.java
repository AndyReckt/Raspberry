package me.andyreckt.raspberry.exception;

public class InvalidExecutorException extends CommandProcessException {
    public final boolean consoleOnly;

    public InvalidExecutorException(boolean consoleOnly) {
        super(false);
        this.consoleOnly = consoleOnly;
    }
}
