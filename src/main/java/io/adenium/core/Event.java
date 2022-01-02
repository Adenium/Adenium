package io.adenium.core;

import org.json.JSONObject;
import io.adenium.utils.HashUtil;

public abstract class Event {
    public abstract void apply();
    public abstract void undo();
    public abstract byte[] getEventBytes();
    public byte[] eventId() {
        return HashUtil.sha256d(getEventBytes());
    }

    public abstract JSONObject toJson();
}
