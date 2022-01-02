package io.adenium.core.events;

import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.encoders.Base58;
import io.adenium.utils.Utils;
import org.json.JSONObject;

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
}
