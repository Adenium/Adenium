package org.wolkenproject.core.script;

import org.wolkenproject.core.Transaction;
import org.wolkenproject.core.script.internal.MochaNumber;
import org.wolkenproject.core.script.internal.MochaObject;

public class Contract extends MochaObject {
    public Contract() {
        super(false);
    }

    public void call(int functionAddress, MochaObject...arguments) {
    }

    // create contract from a transaction payload
    public static final Contract create(Transaction transaction, ProgramCounter programCounter) {
        // create the contract object
        Contract contract = new Contract();

        // create the transaction object
        MochaObject transactionObject = new MochaObject(false);
        transactionObject.addMember(new MochaBool(transaction.hasMultipleSenders()));
        transactionObject.addMember(new MochaBool(transaction.hasMultipleRecipients()));
        transactionObject.addMember(new MochaNumber(transaction.getVersion(), false));
        transactionObject.addMember(new MochaNumber(transaction.getTransactionValue(), false));
        transactionObject.addMember(new MochaNumber(transaction.getTransactionFee(), false));


        // create the stack and populate it
        MochaStack<MochaObject> stack = new MochaStack<>();
        stack.push(contract);
        stack.push(transactionObject);

        // create a scope
        Scope scope = new Scope(transaction, contract, stack, programCounter);

        return contract;
    }
}
