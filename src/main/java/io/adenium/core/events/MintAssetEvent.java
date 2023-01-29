package io.adenium.core.events;

import io.adenium.core.Account;
import io.adenium.core.Context;
import io.adenium.core.Event;
import io.adenium.core.assets.Asset;
import io.adenium.core.transactions.Transaction;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MintAssetEvent extends Event {
    private byte txid[];
    private byte depositAddress[];
    private Asset asset;

    public MintAssetEvent(byte txid[], byte depositAddress[], Asset asset)
    {
        this.txid = txid;
        this.depositAddress = depositAddress;
        this.asset = asset;
    }

    @Override
    public void apply() {
        Context.getInstance().getDatabase().registerAsset(asset);
        try {
            Account.deposit(depositAddress, asset.getUUID(), asset.getSupply());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AdeniumException e) {
            e.printStackTrace();
        }
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
