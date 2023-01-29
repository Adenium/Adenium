package io.adenium.core.events;

import io.adenium.core.Account;
import io.adenium.core.Address;
import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import io.adenium.utils.VarInt;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AdeniumTransferFeeEvent extends Event {
    private byte sender[];
    private byte recipient[];
    private long amount;

    public AdeniumTransferFeeEvent(byte sender[], byte miner[], long fee)
    {
        this.sender = sender;
        this.recipient = miner;
        this.amount = fee;
    }

    public byte[] getSender() {
        return sender;
    }

    public byte[] getRecipient() {
        return recipient;
    }

    @Override
    public void apply() {
        Account sender = Context.getInstance().getDatabase().findAccount(this.sender);
        Account recipt = Context.getInstance().getDatabase().findAccount(this.recipient);

        Context.getInstance().getDatabase().storeAccount(this.sender, sender.withdraw(amount));
        Context.getInstance().getDatabase().storeAccount(this.recipient, recipt.deposit(amount));
    }

    @Override
    public void undo() {
        Account sender = Context.getInstance().getDatabase().findAccount(this.sender);
        Account recipt = Context.getInstance().getDatabase().findAccount(this.recipient);

        Context.getInstance().getDatabase().storeAccount(this.recipient, recipt.withdraw(amount));
        Context.getInstance().getDatabase().storeAccount(this.sender, sender.deposit(amount));
    }

    @Override
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("type", "Adenium Transfer Fee");
        result.put("sender", Address.fromRaw(sender));
        result.put("miner", Address.fromRaw(recipient));
        result.put("amount", amount);
        return result;
    }

    @Override
    public void write(OutputStream stream) throws IOException, AdeniumException {
        stream.write(sender);
        stream.write(recipient);
        VarInt.writeCompactUInt64(amount, false, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, AdeniumException {
        checkFullyRead(stream.read(sender), sender.length);
        checkFullyRead(stream.read(recipient), recipient.length);
        amount = VarInt.readCompactUInt64(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws AdeniumException {
        return (Type) new AdeniumTransferFeeEvent(Address.empty(), Address.empty(), 0);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(AdeniumTransferFeeEvent.class);
    }
}
