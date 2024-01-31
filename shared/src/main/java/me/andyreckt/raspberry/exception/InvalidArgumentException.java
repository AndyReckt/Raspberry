package me.andyreckt.raspberry.exception;

public class InvalidArgumentException extends CommandProcessException {
    public InvalidArgumentException(String message) {
        super(message, true);
    }

    public InvalidArgumentException() {
        super(true);
    }
}
