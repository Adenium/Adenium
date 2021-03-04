package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.ChainMath;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;

public class Block extends BlockHeader {
    private static BigInteger LargestHash = BigInteger.ONE.shiftLeft(256);
    public static int UniqueIdentifierLength = 32;
    private Set<TransactionI>   transactions;

    public Block() {
        this(new byte[32], 0);
    }

    public Block(byte previousHash[], int bits)
    {
        super(Context.getInstance().getNetworkParameters().getVersion(), Utils.timestampInSeconds(), previousHash, new byte[32], bits, 0);
    }

    public final int countLength() {
        return asByteArray().length;
    }

    /*
        returns a new block header
     */
    public final BlockHeader getBlockHeader() {
        return new BlockHeader(getVersion(), getTimestamp(), getParentHash(), getMerkleRoot(), getBits(), getNonce());
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        super.write(stream);
        Utils.writeInt(transactions.size(), stream);
        for (TransactionI transaction : transactions)
        {
            // use serialize here to write transaction serial id
            transaction.serialize(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        super.read(stream);
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            transactions.add(Context.getInstance().getSerialFactory().fromStream(stream));
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Block();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Block.class);
    }

    public TransactionI getCoinbase()
    {
        Iterator<TransactionI> transactions = this.transactions.iterator();
        if (transactions.hasNext())
        {
            return transactions.next();
        }

        return null;
    }

    public int getHeight() {
        TransactionI coinbase = getCoinbase();
        if (coinbase != null)
        {
            return Utils.makeInt(coinbase.getInputs()[0].getData());
        }

        return -1;
    }

    public BigInteger getWork() throws WolkenException {
        return LargestHash.divide(ChainMath.targetIntegerFromBits(getBits()).add(BigInteger.ONE));
    }

    public void addTransaction(TransactionI transaction) {
    }

    public int getTransactionCount() {
        return transactions.size();
    }
}
