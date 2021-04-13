package org.wolkenproject.core;

import org.wolkenproject.core.consensus.CandidateBlock;
import org.wolkenproject.core.consensus.MinedBlockCandidate;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.messages.Inv;
import org.wolkenproject.utils.ChainMath;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractMiner implements Runnable {
    private Address         miningAddress;

    public AbstractMiner(Address miningAddress) {
        this.miningAddress = miningAddress;
    }

    public abstract void mine(Block block) throws WolkenException;

    @Override
    public void run() {
        while (Context.getInstance().isRunning()) {
            try {
                // get a reference parent block
                BlockIndex parent = Context.getInstance().getBlockChain().getBestBlock();
                // generate a new block
                BlockIndex block = Context.getInstance().getBlockChain().fork();
                // mint coins to our address
                block.getBlock().addTransaction(Transaction.newMintTransaction("", ChainMath.getReward(parent.getHeight()), miningAddress));
                // add transactions
                addTransactions(block.getBlock());

                // build the block and calculate all the remaining elements needed
                block.build();

                // mine the block
                mine(block.getBlock());

                if (block.getBlock().verifyProofOfWork()) {
                    // create a candidate
                    CandidateBlock candidateBlock = new MinedBlockCandidate(Context.getInstance(), block);

                    // submit the block
                    Context.getInstance().getBlockChain().suggest(candidateBlock);

                    // make a collection
                    Collection<byte[]> list = new ArrayList<>();
                    list.add(block.getHash());

                    // broadcast the block
                    Context.getInstance().getServer().broadcast(new Inv(Inv.Type.Block, list));
                }
            } catch (WolkenException e) {
                e.printStackTrace();
            }
        }
    }

    protected void addTransactions(Block block) {
        while (block.calculateSize() < Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
            block.addTransaction(Context.getInstance().getTransactionPool().pollTransaction());
        }

        if (block.calculateSize() > Context.getInstance().getNetworkParameters().getMaxBlockSize()) {
            block.removeLastTransaction();
        }
    }
}
