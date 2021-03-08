package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.InvalidOpcodeException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OpcodeList {
    // this class should be used to store opcodes
    // it will compact them in the most optimal
    // way for storage/network transfers to help
    // reduce the cost of transactions.

    // contains bits from arguments
    private long    argumentBits[];
    private Opcode  opcodes[];

    public OpcodeList() {
    }

    public void read(OpcodeRegister register, byte opcodes[]) {
        int counter = 0;

        while (counter < opcodes.length) {
            Opcode opcode = register.getOpcode(opcodes[counter ++]);
            if (opcode == null) {
                throw new InvalidOpcodeException();
            }
        }
    }
}
