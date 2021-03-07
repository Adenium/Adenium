package org.wolkenproject.core.script;

import org.wolkenproject.encoders.Base16;

import java.util.ArrayList;
import java.util.List;

public class MochaClass {
    private List<MochaFunction> functions;

    public MochaClass() {
        functions = new ArrayList<>();
        addFunction("hashCode", (mem)->{ return new MochaByteArray(mem.popStack().checksum()); });
        addFunction("toString", (mem)->{ return new MochaString(Base16.encode(mem.popStack().checksum())); });
    }

    public void addFunction(String functionName, MochaFunction function) {
        functions.add(function);
    }

    // call any functions defined in this class
    public MochaObject call(int functionPtr, MemoryModule memoryModule) {
        return null;
    }
}
