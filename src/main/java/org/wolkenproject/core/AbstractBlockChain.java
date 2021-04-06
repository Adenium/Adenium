package org.wolkenproject.core;

import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractBlockChain implements Runnable {
    private final Context       context;
    private final ReentrantLock mutex;

    public AbstractBlockChain(Context context) {
        this.context    = context;
        this.mutex      = new ReentrantLock();
    }

    public abstract boolean verifyBlock(BlockIndex block);

    public Context getContext() {
        return context;
    }

    public ReentrantLock getMutex() {
        return mutex;
    }

    private void setBlock(int height, BlockIndex block) {
        BlockIndex previousIndex = getContext().getDatabase().findBlock(height);
        if (previousIndex != null) {
            addStale(previousIndex);
        }

        getContext().getDatabase().storeBlock(height, block);
    }

    // stales block.
    protected abstract void addStale(BlockIndex block);
}
