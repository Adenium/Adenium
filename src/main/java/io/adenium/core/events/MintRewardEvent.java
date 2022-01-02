package io.adenium.core.events;

import org.json.JSONObject;
import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.encoders.Base58;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import io.adenium.utils.Utils;
import io.adenium.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MintRewardEvent extends Event {
    private byte address[];
    private long amount;

    public MintRewardEvent(byte address[], long amount) {
        this.address    = address;
        this.amount     = amount;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().storeAccount(address,
                Context.getInstance().getDatabase().findAccount(address).deposit(amount));
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().storeAccount(address,
                Context.getInstance().getDatabase().findAccount(address).undoDeposit(amount));
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Mint".getBytes(), address, Utils.takeApartLong(amount));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address)).put("amount", amount);
    }

    @Override
    public void write(OutputStream stream) throws IOException, AdeniumException {
        stream.write(address);
        VarInt.writeCompactUInt64(amount, false, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, AdeniumException {
        checkFullyRead(stream.read(address), address.length);
        amount = VarInt.readCompactUInt64(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws AdeniumException {
        return (Type) new MintRewardEvent(new byte[address.length], 0);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(MintRewardEvent.class);
    }
}
