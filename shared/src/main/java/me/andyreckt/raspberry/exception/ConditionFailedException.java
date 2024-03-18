package me.andyreckt.raspberry.exception;

public class ConditionFailedException extends CommandProcessException {
    public ConditionFailedException(String message) {
        super(message, true);
    }

    public ConditionFailedException() {
        super(true);
    }
}
