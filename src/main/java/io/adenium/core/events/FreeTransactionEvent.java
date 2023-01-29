package io.adenium.core.events;

import io.adenium.core.Event;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FreeTransactionEvent extends Event {
    public FreeTransactionEvent(byte[] address) {
    }

    @Override
    public void apply() {
    }

    @Override
    public void undo() {
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void write(OutputStream stream) throws IOException, AdeniumException {
    }

    @Override
    public void read(InputStream stream) throws IOException, AdeniumException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws AdeniumException {
        return null;
    }

    @Override
    public int getSerialNumber() {
        return 0;
    }
}
