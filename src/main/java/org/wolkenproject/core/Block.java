package org.wolkenproject.core;

import org.wolkenproject.core.transactions.MintTransaction;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.*;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Block extends SerializableI implements Iterable<Transaction> {
    private static BigInteger       LargestHash             = BigInteger.ONE.shiftLeft(256);
    public static int               UniqueIdentifierLength  = 32;
    private BlockHeader             blockHeader;
    private Set<Transaction>        transactions;
    private BlockStateChangeResult  stateChange;

    public Block() {
        this(new byte[32], 0);
    }

    public Block(byte previousHash[], int bits)
    {
        blockHeader = new BlockHeader(Context.getInstance().getNetworkParameters().getVersion(), Utils.timestampInSeconds(), previousHash, new byte[32], bits, 0);
        transactions = new LinkedHashSet<>();
    }

    public final int calculateSize() {
        int transactionLength = 0;
        for (Transaction transaction : transactions) {
            transactionLength += transaction.calculateSize();
        }

        return BlockHeader.Size + VarInt.sizeOfCompactUin32(transactions.size(), false) + transactionLength;
    }

    public final int calculateSizeLocalStorage() {
        int transactionLength = 0;
        for (Transaction transaction : transactions) {
            transactionLength += transaction.calculateSize();
        }

        return blockHeader.calculateSize() + VarInt.sizeOfCompactUin32(transactions.size(), false) + transactionLength;
    }

    /*
        returns a new block header
     */
    public final BlockHeader getBlockHeader() {
        return new BlockHeader(getVersion(), getTimestamp(), getParentHash(), getMerkleRoot(), getBits(), getNonce());
    }

    // executes transctions and returns an event list
    public BlockStateChangeResult getStateChange() {
        return stateChange;
    }

    // call transaction.verify()
    // this does not mean that transactions are VALID
    private boolean shallowVerifyTransactions() {
        try {
            for (Transaction transaction : transactions) {
                if (!transaction.shallowVerify()) {
                    return false;
                }
            }
        } catch (WolkenException e) {
            return false;
        }

        return true;
    }

    public void build(int blockHeight) throws WolkenException {
        // set the combined merkle root
        setMerkleRoot(getStateChange().getMerkleRoot());
    }

    public boolean verify(int blockHeight) {
        // PoW check
        if (!blockHeader.verifyProofOfWork()) return false;
        // must have at least one transaction
        if (transactions.isEmpty()) return false;
        // first transaction must be a minting transaction
        if (transactions.iterator().next() instanceof MintTransaction == false) return false;
        // shallow transaction checks
        if (!shallowVerifyTransactions()) return false;
        // create a state change object and verify transactions
        if (!createSateChange(blockHeight)) return false;
        // merkle tree checks
        if (!Utils.equals(getStateChange().getMerkleRoot(), getMerkleRoot())) return false;

        return true;
    }

    // creates a state change without verifying transactions
    private void createSateChange() {
        BlockStateChange blockStateChange = new BlockStateChange();
        try {
            for (Transaction transaction : transactions) {
                transaction.getStateChange(this, blockStateChange);
                blockStateChange.addTransaction(transaction.getHash());
            }
        } catch (WolkenException e) {
            stateChange = new BlockStateChangeResult(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
            return;
        }

        stateChange = blockStateChange.getResult();
    }

    // creates a state change and verifies transactions
    private boolean createSateChange(int blockHeight) {
        BlockStateChange blockStateChange = new BlockStateChange();

        long fees = 0L;

        for (Transaction transaction : transactions) {
            fees += transaction.getTransactionFee();
        }

        for (Transaction transaction : transactions) {
            if (!transaction.verify(blockStateChange, this, blockHeight, fees)) {
                return false;
            }

            try {
                transaction.getStateChange(this, blockStateChange);
            } catch (WolkenException e) {
                return false;
            }

            blockStateChange.addTransaction(transaction.getHash());
        }

        stateChange = blockStateChange.getResult();
        return true;
    }

    public byte[] getHashCode() {
        return blockHeader.getHashCode();
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        blockHeader.write(stream);
        VarInt.writeCompactUInt32(transactions.size(), false, stream);
        for (Transaction transaction : transactions)
        {
            // use serialize here to write transaction serial id
            transaction.serialize(stream);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        blockHeader.read(stream);
        int length = VarInt.readCompactUInt32(false, stream);

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

    public Transaction getCoinbase()
    {
        Iterator<Transaction> transactions = this.transactions.iterator();
        if (transactions.hasNext())
        {
            return transactions.next();
        }

        return null;
    }

    public BigInteger getWork() {
        return LargestHash.divide(getTargetInteger().add(BigInteger.ONE));
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public void removeLastTransaction() {
        Iterator<Transaction> transactions = this.transactions.iterator();
        if (transactions.hasNext())
        {
            transactions.next();

            if (!transactions.hasNext()) {
                transactions.remove();
            }
        }
    }

    @Override
    public Iterator<Transaction> iterator() {
        return transactions.iterator();
    }

    public long getFees() {
        long fees = 0L;

        for (Transaction transaction : transactions) {
            fees += transaction.getTransactionFee();
        }

        return fees;
    }

    public Set<byte[]> getPrunedTransactions() {
        Set<byte[]> pruned = new LinkedHashSet<>();
        for (Transaction transaction : transactions) {
            pruned.add(transaction.getHash());
        }

        return pruned;
    }

    public int getEventCount() {
        return 0;
    }

    public long getTotalValue() {
        return 0;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public byte[] getSerializedTransactions() throws IOException, WolkenException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Transaction transaction : transactions) {
            transaction.serialize(outputStream);
        }

        return outputStream.toByteArray();
    }

    public byte[] getSerializedEvents() throws IOException, WolkenException {
        return null;
    }

    public void write(OutputStream outputStream, boolean writeLocally) throws IOException, WolkenException {
        write(outputStream);
        // start writing events
    }

    public PrunedBlock getPruned() throws WolkenException {
        return new PrunedBlock(getBlockHeader(), getStateChange().getTransactionIds(), getStateChange().getTransactionEventIds());
    }

    public void setNonce(int nonce) {
        blockHeader.setNonce(nonce);
    }

    protected void setMerkleRoot(byte[] merkleRoot) {
        blockHeader.setMerkleRoot(merkleRoot);
    }

    public void setParent(byte[] parent) {
        blockHeader.setParent(parent);
    }

    public void setBits(int bits) {
        blockHeader.setBits(bits);
    }

    public int getVersion() {
        return blockHeader.getVersion();
    }

    public int getTimestamp() {
        return blockHeader.getTimestamp();
    }

    public byte[] getParentHash() {
        return blockHeader.getParentHash();
    }

    public byte[] getMerkleRoot() {
        return blockHeader.getMerkleRoot();
    }

    public int getNonce() {
        return blockHeader.getNonce();
    }

    public int getBits() {
        return blockHeader.getBits();
    }

    public byte[] getTargetBytes() {
        return blockHeader.getTargetBytes();
    }

    public BigInteger getTargetInteger() {
        return blockHeader.getTargetInteger();
    }

    public byte[] getHeaderBytes() {
        return blockHeader.asByteArray();
    }

    public boolean verifyProofOfWork() {
        return blockHeader.verifyProofOfWork();
    }
}
