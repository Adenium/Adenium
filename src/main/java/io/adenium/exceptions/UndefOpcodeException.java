package io.adenium.exceptions;

public class UndefOpcodeException extends MochaException {
    public UndefOpcodeException(String op) {
        super("Undefined OpCode '" + op + "'.");
    }
    public UndefOpcodeException(int op) {
        super("Undefined OpCode '" + op + "'.");
    }
    public UndefOpcodeException() {
        super("");
    }
}
