package org.wolkenproject.core.transactions;

import org.json.JSONObject;
import org.wolkenproject.core.*;
import org.wolkenproject.core.assets.Asset;
import org.wolkenproject.crypto.Signature;
import org.wolkenproject.crypto.ec.RecoverableSignature;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RegisterAsset extends Transaction {
    // nonce
    private long nonce;
    // fee to register the alia
    private long fee;
    // alias
    private Asset asset;
    // signature of the sender
    private RecoverableSignature signature;

    protected RegisterAsset() {
        this(0, 0, null);
    }

    protected RegisterAsset(long nonce, long fee, Asset asset) {
        this.nonce  = nonce;
        this.fee    = fee;
        this.asset  = asset;
        this.signature = new RecoverableSignature();
    }

    @Override
    public int getFlags() {
        return 0;
    }

    @Override
    public long getTransactionValue() {
        return Context.getInstance().getNetworkParameters().getAliasRegistrationCost();
    }

    @Override
    public long getTransactionFee() {
        return fee;
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
    public boolean shallowVerify() {
        // this is not 100% necessary
        // a transfer of 0 with a fee of 0 is not allowed
        try {
            return
                    fee > 0 &&
                    (Context.getInstance().getDatabase().findAccount(getSender().getRaw()).getNonce() + 1) == nonce &&
                    (signature.getR().length == 32) &&
                    (signature.getS().length == 32) &&
                    getSender() != null &&
                    !Context.getInstance().getDatabase().findAccount(getSender().getRaw()).hasAlias();
        } catch (WolkenException e) {
        }

        return false;
    }

    @Override
    public Address getSender() throws WolkenException {
        return Address.fromKey(signature.recover(asByteArray()));
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
    public long calculateSize() {
        return
                VarInt.sizeOfCompactUin32(getVersion(), false) +
                VarInt.sizeOfCompactUin64(nonce, false) + 65;
    }

    @Override
    public boolean verify(BlockStateChange blockStateChange, Block block, int blockHeight, long fees) {
        return false;
    }

    @Override
    public void getStateChange(Block block, BlockStateChange stateChange) throws WolkenException {
        Address sender = getSender();
        stateChange.createAccountIfDoesNotExist(sender.getRaw());
    }

    @Override
    public JSONObject toJson(boolean txEvt, boolean evHash) {
        JSONObject txHeader = new JSONObject().put("name", getClass().getName()).put("version", getVersion());
        txHeader.put("content", new JSONObject().put("nonce", nonce).put("fee", fee).put("v", signature.getV()).put("r", Base16.encode(signature.getR())).put("s", Base16.encode(signature.getS())));
        return txHeader;
    }

    @Override
    protected void setSignature(Signature signature) throws WolkenException {
        if (signature instanceof RecoverableSignature) {
            this.signature = (RecoverableSignature) signature;
        }

        throw new WolkenException("invalid signature type '" + signature.getClass() + "'.");
    }

    @Override
    protected Transaction copyForSignature() {
        return new RegisterAsset(nonce, fee, asset);
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        signature.write(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        signature.read(stream);
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RegisterAsset();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(MintTransaction.class);
    }
}