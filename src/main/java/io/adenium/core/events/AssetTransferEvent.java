package io.adenium.core.events;

import io.adenium.core.Account;
import io.adenium.core.Address;
import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.core.assets.Asset;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import io.adenium.utils.VarInt;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class AssetTransferEvent extends Event {
    private byte sender[];
    private byte recipient[];
    private byte uuid[];
    private BigInteger amount;

    public AssetTransferEvent(byte sender[], byte recipient[], byte uuid[], BigInteger amount)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.uuid = uuid;
        this.amount = amount;
    }

    public byte[] getSender() {
        return sender;
    }

    public byte[] getRecipient() {
        return recipient;
    }

    public byte[] getUuid() {
        return uuid;
    }

    @Override
    public void apply() {
        try {
            Account.withdraw(sender, uuid, amount);
            Account.deposit(recipient, uuid, amount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AdeniumException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void undo() {
        try {
            Account.withdraw(recipient, uuid, amount);
            Account.deposit(sender, uuid, amount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AdeniumException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("type", "asset transfer");
        result.put("sender", Address.fromRaw(sender));
        result.put("recipient", Address.fromRaw(recipient));
        result.put("asset", Context.getInstance().getDatabase().findAsset(uuid).getName());
        result.put("amount", amount.toString());
        return result;
    }

    @Override
    public void write(OutputStream stream) throws IOException, AdeniumException {
        stream.write(sender);
        stream.write(recipient);
        stream.write(uuid);
        VarInt.writeCompactUint256(amount, false, stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, AdeniumException {
        checkFullyRead(stream.read(sender), sender.length);
        checkFullyRead(stream.read(recipient), recipient.length);
        checkFullyRead(stream.read(uuid), uuid.length);
        amount = VarInt.readCompactUint256(false, stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws AdeniumException {
        return (Type) new AssetTransferEvent(Address.empty(), Address.empty(), Asset.emptyUUID(), BigInteger.ZERO);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(AssetTransferEvent.class);
    }
}
