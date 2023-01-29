package io.adenium.core.transactions;

import io.adenium.core.Address;
import io.adenium.core.Block;
import io.adenium.core.BlockStateChange;
import io.adenium.core.assets.Asset;
import io.adenium.crypto.Signature;
import io.adenium.exceptions.AdeniumException;
import io.adenium.serialization.SerializableI;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MakeAssetTransaction extends Transaction {
    private Asset           asset;
    private long            fee;

    public MakeAssetTransaction(Asset asset)
    {
        this.asset      = asset;
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public long getTransactionValue() {
        return 0;
    }

    @Override
    public long getTransactionFee() {
        return 0;
    }

    @Override
    public long getMaxUnitCost() {
        return 0;
    }

    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public TransactionCode checkTransaction() {
        return null;
    }

    @Override
    public Address getSender() throws AdeniumException {
        return null;
    }

    @Override
    public Address getRecipient() {
        return null;
    }

    @Override
    public boolean hasMultipleSenders() {
        return false;
    }

    @Override
    public boolean hasMultipleRecipients() {
        return false;
    }

    @Override
    public int calculateSize() {
        return 0;
    }

    @Override
    public boolean verify(BlockStateChange blockStateChange, Block block, int blockHeight, long fees) {
        return false;
    }

    @Override
    public void getStateChange(Block block, BlockStateChange stateChange) throws AdeniumException {
    }

    @Override
    public JSONObject toJson(boolean txEvt, boolean evHash) {
        return null;
    }

    @Override
    protected void setSignature(Signature signature) throws AdeniumException {
    }

    @Override
    protected Transaction copyForSignature() {
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
