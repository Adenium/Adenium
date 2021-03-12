package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaObject;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the contract in which the point of entry exists
    private Contract            contract;
    // the stack
    private Stack<MochaObject>  stack;
    // tell all subprocesses to continue running
    private AtomicBoolean       keepRunning;
    // an interrupt signal (if any)
    private AtomicInteger       interruptSignal;

    public Scope(VirtualMachine virtualMachine, Contract contract, Stack<MochaObject> stack) {
        this.contract   = contract;
        this.stack      = stack;
    }

    public Contract getContract() {
        return contract;
    }

    public Stack<MochaObject> getStack() {
        return stack;
    }

    public void stopProcesses(int signal) {
        keepRunning.set(false);
        interruptSignal.set(signal);
    }
}
