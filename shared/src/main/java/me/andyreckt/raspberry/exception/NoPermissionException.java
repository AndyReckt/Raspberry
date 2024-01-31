package me.andyreckt.raspberry.exception;

public class NoPermissionException extends CommandProcessException {
    public NoPermissionException() {
        super(false);
    }
}
