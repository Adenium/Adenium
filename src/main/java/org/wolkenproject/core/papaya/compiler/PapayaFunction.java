package org.wolkenproject.core.papaya.compiler;

import org.wolkenproject.core.papaya.AccessModifier;

import java.io.OutputStream;
import java.util.Set;

public class PapayaFunction extends PapayaMember {
    private final String                name;
    private final Set<PapayaField>      arguments;
    private final PapayaStatement       statement;
    private byte                        byteCode[];
    private final LineInfo              lineInfo;

    public PapayaFunction(AccessModifier accessModifier, String name, Set<PapayaField> arguments, PapayaStatement statement, LineInfo lineInfo) {
        super(accessModifier, name);
        this.name = name;
        this.arguments = arguments;
        this.statement = statement;
        this.lineInfo = lineInfo;
        this.byteCode = new byte[0];
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public String getName() {
        return name;
    }

    public Set<PapayaField> getArguments() {
        return arguments;
    }

    public PapayaStatement getStatement() {
        return statement;
    }

    public byte[] getByteCode() {
        return byteCode;
    }
}
