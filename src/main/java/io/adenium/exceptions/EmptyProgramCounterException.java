package io.adenium.exceptions;

public class EmptyProgramCounterException extends MochaException {
    public EmptyProgramCounterException() {
        super("");
    }
}
