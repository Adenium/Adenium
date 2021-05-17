package org.wolkenproject.papaya.archive;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.*;
import org.wolkenproject.papaya.parser.Node;
import org.wolkenproject.utils.ByteArray;

import java.util.List;

public class ArchivedMethod {
    private final String                        name;
    private final String                        type;
    private final List<ArchivedMember>          arguments;
    private final AccessModifier                accessModifier;
    private final boolean                       isStatic;
    private final LineInfo                      lineInfo;
    private final Node                          methodBody;

    public ArchivedMethod(String name, String typeName, List<ArchivedMember> arguments, AccessModifier accessModifier, boolean isStatic, LineInfo lineInfo, Node methodBody) {
        this.name = name;
        this.type = typeName;
        this.arguments = arguments;
        this.accessModifier = accessModifier;
        this.isStatic = isStatic;
        this.lineInfo = lineInfo;
        this.methodBody = methodBody;
    }

    public PapayaMember compile(CompilationScope compilationScope) throws PapayaException {
        ByteArray functionIdentifier = null;
        ByteArray typeIdentifier = null;

        FunctionScope scope = compilationScope.enterFunction(this);

        for (ArchivedMember member : arguments) {
            scope.declare(member);
        }

        for (Node node : methodBody) {
            scope.traverse(node, scope, compilationScope);
        }

        return new PapayaMember(accessModifier, isStatic, functionIdentifier, typeIdentifier, lineInfo, compilationScope.exitFunction());
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<ArchivedMember> getArguments() {
        return arguments;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public Node getMethodBody() {
        return methodBody;
    }

    public String formattedString(int i) {
        StringBuilder builder = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        for (int t = 0; t < i; t ++) {
            tabs.append("\t");
        }

        builder.append(tabs).append(type).append(" ").append(name).append(" ").append(accessModifier).append(" ").append(isStatic?"static":"").append(" ").append(lineInfo).append("\n");
        builder.append(tabs).append("\t\t").append(arguments).append("\n");
        if (methodBody != null) {
            methodBody.toString(i + 2, builder);
        }

        return builder.toString();
    }

    public String formattedString() {
        return formattedString(0);
    }
}
