package me.andyreckt.raspberry.exception;

public class UnknownCommandException extends CommandProcessException {
    public UnknownCommandException(boolean help) {
        super(help);
    }
}
