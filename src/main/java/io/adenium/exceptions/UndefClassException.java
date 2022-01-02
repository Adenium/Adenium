package io.adenium.exceptions;

public class UndefClassException extends MochaException {
    public UndefClassException(String msg) {
        super(msg);
    }

    public UndefClassException(Throwable msg) {
        super(msg);
    }
}
