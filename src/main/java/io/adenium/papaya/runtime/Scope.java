package io.adenium.papaya.runtime;

import io.adenium.papaya.compiler.Struct;
import io.adenium.core.Block;
import io.adenium.core.BlockIndex;
import io.adenium.core.transactions.Transaction;
import io.adenium.exceptions.ContractOutOfFundsExceptions;
import io.adenium.exceptions.InvalidTransactionException;
import io.adenium.exceptions.PapayaException;
import io.adenium.script.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scope {
    // the 'container' block
    private BlockIndex                          block;
    // the 'caller' transaction
    private Transaction                         caller;
    // the stack
    private Stack<PapayaStack<PapayaHandler>>   stack;
    // tell all subprocesses to continue running
    private AtomicBoolean                       keepRunning;
    // an interrupt signal (if any)
    private AtomicInteger                       interruptSignal;
    // the program counter
    private ProgramCounter                      programCounter;
    private PapayaHandler                       nullreference;

    public Scope(Block block, Transaction caller, ProgramCounter programCounter) {
        this.caller     = caller;
        this.stack      = new Stack<>();
        this.programCounter = programCounter;
        this.nullreference  = new DefaultHandler(new PapayaObject());
    }

    /*
        Execute a function.
        Returns the amount of Adenium used to execute this transaction.
     */
    public TerminationSignal startProcess(long gasLimit, long gasPrice) throws InvalidTransactionException, PapayaException, ContractOutOfFundsExceptions {
        long availableGas = gasLimit;

        while (getProgramCounter().hasNext() && keepRunning.get()) {
            // get the next opcode and get it ready for execution.
            OpcodeDefinition opcode = getProgramCounter().next();

            // if the available gas is greater than the cost of this opcode then execute it.
            if (availableGas >= opcode.getWeight()) {
                // execute the opcode.
                opcode.execute(this);
                // reduce the available gas.
                availableGas -= opcode.getWeight();
                continue;
            }

            throw new ContractOutOfFundsExceptions();
        }

        long usedGas = gasLimit - availableGas;

        return new TerminationSignal(gasLimit, gasPrice, usedGas, interruptSignal.get());
    }

    public void stopProcesses(int signal) {
        keepRunning.set(false);
        interruptSignal.set(signal);
    }

    public PapayaStack<PapayaHandler> getStack() {
        return stack.peek();
    }

    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

//    public void checkSig() throws MochaException {
//        MochaObject address     = getStack().pop();
//        MochaObject signature   = getStack().pop();
//        byte signatureData[]    = getSignatureData();
//
//        Key key                 = ((MochaCryptoSignature) signature).getSignature().checkSignature(signatureData);
//
//        if (signature instanceof MochaCryptoSignature) {
//            getStack().push(new MochaBool(false));
//            return;
//        }
//
//        getStack().push(((MochaPublicKey) publicKey).checkSignature((MochaCryptoSignature) signature, signatureData));
//    }

    public void verify() throws InvalidTransactionException, PapayaException {
        if (!getStack().pop().asBool()) {
            throw new InvalidTransactionException();
        }
    }

    protected byte[] getSignatureData() {
        return null;
    }

    public void destroyContract() throws PapayaException {
        // this is the address that we will send any remaining funds to
//        PapayaObject address = getStack().pop();
//
//        if (address instanceof MochaAddress) {
//        }

        throw new PapayaException("invalid address provided.");
    }

    public Stack<Struct> getStackTrace() {
        return null;
    }

    public void callOperator(int operator) throws PapayaException {
        getStack().peek().getStructure().getOperator(operator).call(this);
    }

    public PapayaHandler getNullReference() {
        return nullreference;
    }

    public void makeTuple(int size) throws PapayaException {
        List<PapayaHandler> tuple = new ArrayList<>(size);
        for (int i = size - 1; i > -1; i --) {
            tuple.set(i, stack.peek().pop());
        }

        stack.peek().push(new DefaultHandler(new PapayaTuple(tuple)));
    }

    public void rebase() {
        stack.push(new PapayaStack<>());
    }

    public void drop() {
        stack.pop();
    }

    public void dropKeep(int amount) {
        stack.pop();
    }
}
