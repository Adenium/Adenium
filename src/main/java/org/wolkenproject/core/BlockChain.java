package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Message;
import org.wolkenproject.network.messages.BlockList;
import org.wolkenproject.network.messages.RequestBlocks;
import org.wolkenproject.utils.Utils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements Runnable {
    private BlockIndex          tip;
    private byte[]              chainWork;
    // contains random blocks sent from peers.
    private Queue<BlockIndex>   orphanedBlocks;
    private static final int    MaximumBlockQueueSize = 1_000_000_000;

    private ReentrantLock   lock;

    public final void consensus()
    {
    }

    @Override
    public void run() {
        lock.lock();
        try {
            while (!orphanedBlocks.isEmpty()) {
                BlockIndex block = orphanedBlocks.poll();
                if (block.getChainWork().compareTo(tip.getChainWork()) > 0) {
                    // switch to this chain if it's valid
                    if (block.validate()) {
                        if (block.getHeight() == tip.getHeight()) {
                            // if both blocks share the same height, then orphan the current tip.
                            replaceTip(block);
                        } else if (block.getHeight() == (tip.getHeight() + 1)) {
                            // if block is next in line then set as next block.
                            setNext(block);
                        } else if (block.getHeight() > tip.getHeight()) {
                            // if block next but with some blocks missing then we fill the gap.
                            setNextGapped(block);
                        } else if (block.getHeight() < tip.getHeight()) {
                            // if block is earlier then we must roll back the chain.
                            rollback(block);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void replaceTip(BlockIndex block) {
        addOrphan(tip);

        byte previousHash[] = tip.getBlock().getParentHash();
        if (Utils.equals(block.getBlock().getParentHash(), previousHash)) {
            setTip(block);
            return;
        }

        rollbackIntoExistingParent(block.getBlock().getParentHash(), block.getBlock().getHeight() - 1);
    }

    private boolean rollbackIntoExistingParent(byte[] parentHash, int height) {
        // check that the block exists
        if (Context.getInstance().getDatabase().checkBlockExists(parentHash)) {
            return true;
        }

        // we must request it in case it doesn't
        BlockIndex parent = requestBlock(parentHash);
        while (parent != null) {
            replaceBlockIndex(height, parent);
            if (Context.getInstance().getDatabase().checkBlockExists(parentHash)) {
                updateIndices(parent);
                return true;
            }
        }

        return false;
    }

    private void updateIndices(BlockIndex index) {
        while (index != null) {
            index.recalculateChainWork();
        }
    }

    private void replaceBlockIndex(int height, BlockIndex block) {
        BlockIndex previousIndex = Context.getInstance().getDatabase().findBlock(height);
        if (previousIndex != null) {
            addOrphan(previousIndex);
        }
        Context.getInstance().getDatabase().setBlockIndex(height, block);
    }

    private BlockIndex requestBlock(byte hash[]) {
        Message request = new RequestBlocks(Context.getInstance().getNetworkParameters().getVersion(), hash);
        Message response= Context.getInstance().getServer().broadcastRequest(request);

        if (response != null && response instanceof BlockList) {
            Collection<BlockIndex> blocks = response.getPayload();
            if (blocks != null && !blocks.isEmpty()) {
                blocks.iterator().next();
            }
        }

        return null;
    }

    private void setTip(BlockIndex block) {
        tip = block;
        Context.getInstance().getDatabase().setTip(block);
    }

    private void addOrphan(BlockIndex block) {
        orphanedBlocks.add(block);

        // calculate the maximum blocks allowed in the queue.
        int maximumBlocks = MaximumBlockQueueSize / Context.getInstance().getNetworkParameters().getMaxBlockSize();

        // remove any blocks that are too far back in the queue.
        if (orphanedBlocks.size() > maximumBlocks) {
            trimOrphans(orphanedBlocks.size() - maximumBlocks);
        }
    }

    private void trimOrphans(int count) {
        for (int i = 0; i < count; i ++) {
            orphanedBlocks.remove(orphanedBlocks.size() - 1);
        }
    }

    public BlockIndex makeGenesisBlock() throws WolkenException {
        Block genesis = new Block(new byte[Block.UniqueIdentifierLength], 0);
        genesis.addTransaction(TransactionI.newCoinbase(0, "", Context.getInstance().getNetworkParameters().getMaxReward(), Context.getInstance().getPayList()));
        return new BlockIndex(genesis, BigInteger.ZERO, 0);
    }

    public BlockIndex makeBlock() throws WolkenException {
        lock.lock();
        try {
            return tip.generateNextBlock();
        }
        finally {
            lock.unlock();
        }
    }
}
