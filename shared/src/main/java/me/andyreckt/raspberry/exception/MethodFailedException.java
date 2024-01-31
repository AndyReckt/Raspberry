package me.andyreckt.raspberry.exception;

public class MethodFailedException extends CommandProcessException {
    public Throwable cause;
    public MethodFailedException(String message, Throwable cause) {
        super(message, false);
        this.cause = cause;
    }


    public MethodFailedException(Throwable cause) {
        super(false);
        this.cause = cause;
    }
}
