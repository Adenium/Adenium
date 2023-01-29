package io.adenium.core.assets;

import io.adenium.core.transactions.Transaction;
import io.adenium.exceptions.InvalidAssetImplementation;
import io.adenium.serialization.SerializableI;
import io.adenium.serialization.SerializationFactory;
import io.adenium.exceptions.AdeniumException;
import io.adenium.utils.HashUtil;
import io.adenium.utils.Utils;
import io.adenium.utils.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public abstract class Asset extends SerializableI {
    public final static int     UniqueIdentifierLength = 20;
    public final static byte    DefaultUUID[] = new byte[UniqueIdentifierLength];

    // the name of the asset.
    private String        name;
    // a short description of the asset.
    private String        desc;
    // 32 byte transaction id for creating the asset.
    private byte          txid[];

    // default constructor
    public Asset(String name, String desc, byte[] txid) throws InvalidAssetImplementation {
        byte shortNameBytes[] = name.getBytes(StandardCharsets.UTF_8);
        if (shortNameBytes.length > 32) throw new InvalidAssetImplementation("Short name is too long");

        byte descriptionBytes[] = desc.getBytes(StandardCharsets.UTF_8);
        if (descriptionBytes.length > 1024) throw new InvalidAssetImplementation("Description is too long");

        this.name       = name;
        this.desc       = desc;
        this.txid       = Utils.copyOf(txid);
    }

    public void setTransactionId(byte txid[]) {
        this.txid = txid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public byte[] getTransactionId() {
        return txid;
    }

    public byte[] getUUID() {
        byte name_sha256[] = HashUtil.sha256(name.getBytes(StandardCharsets.UTF_8));
        byte desc_sha256[] = HashUtil.sha256(desc.getBytes(StandardCharsets.UTF_8));
        byte ndsc_sha256[] = HashUtil.sha256(Utils.concatenate(name_sha256, desc_sha256));

        return HashUtil.hash160(HashUtil.sha256(Utils.concatenate(ndsc_sha256, txid)));
    }

    @Override
    public void write(OutputStream stream) throws IOException, AdeniumException {
        stream.write(name.length());
        stream.write(name.getBytes(StandardCharsets.UTF_8));
        VarInt.writeCompactUInt32(desc.length(), false, stream);
        stream.write(desc.getBytes(StandardCharsets.UTF_8));
        stream.write(txid);
        writeContent(stream);
    }

    @Override
    public void read(InputStream stream) throws IOException, AdeniumException {
        int length = 0;
        length = stream.read();
        if (length < 0) {
            throw new AdeniumException("invalid name length.");
        }
        byte array[] = new byte[length];
        checkFullyRead(stream.read(array), length);
        name = new String(array);
        length = VarInt.readCompactUInt32(false, stream);
        array = new byte[length];
        checkFullyRead(stream.read(array), length);
        desc = new String(array);
        checkFullyRead(stream.read(txid), txid.length);
        readContent(stream);
    }

    public abstract boolean isTransferable();
    public abstract boolean isFungible();
    public abstract BigInteger getSupply();
    public abstract BigInteger getTotalSupply();

    public abstract void writeContent(OutputStream stream) throws IOException, AdeniumException;
    public abstract void readContent(InputStream stream) throws IOException, AdeniumException;

    public static void register(SerializationFactory serializationFactory) throws InvalidAssetImplementation {
        // register all asset types here.
    }

    public static final byte[] emptyUUID() {
        return new byte[UniqueIdentifierLength];
    }
}
