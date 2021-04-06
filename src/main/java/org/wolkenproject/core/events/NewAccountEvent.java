package org.wolkenproject.core.events;

import org.json.JSONObject;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.Event;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewAccountEvent extends Event {
    private byte    address[];

    public NewAccountEvent(byte[] address) {
        super();
        this.address    = address;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().newAccount(address);
    }

    @Override
    public void undo() {
        Context.getInstance().getDatabase().removeAccount(address);
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Account Registration".getBytes(), address);
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address));
    }

    public byte[] getAddress() {
        return address;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        stream.write(address);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        checkFullyRead(stream.read(address), address.length);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new NewAccountEvent(new byte[address.length]);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(NewAccountEvent.class);
    }
}
