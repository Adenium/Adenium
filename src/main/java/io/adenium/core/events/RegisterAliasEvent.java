package io.adenium.core.events;

import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.encoders.Base58;
import io.adenium.utils.Utils;
import org.json.JSONObject;

public class RegisterAliasEvent extends Event {
    private byte    address[];
    private long    alias;

    public RegisterAliasEvent(byte[] address, long alias) {
        super();
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().registerAlias(address, alias);
    }

    @Override
    public void undo() {
    }

    @Override
    public byte[] getEventBytes() {
        return Utils.concatenate("Register Alias".getBytes(), address, Utils.takeApartLong(alias));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("event", this.getClass().getName()).put("address", Base58.encode(address)).put("alias", alias);
    }

    public long getAlias() {
        return alias;
    }
}
