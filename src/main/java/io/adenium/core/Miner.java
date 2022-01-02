package io.adenium.core;

import io.adenium.exceptions.WolkenException;
import io.adenium.utils.ChainMath;

public class Miner extends AbstractMiner {
    public Miner(Address miningAddress) {
        super(miningAddress);
    }

    @Override
    public void mine(Block block) throws WolkenException {
        BlockHeader header  = block;
        byte hash[]         = header.getHashCode();

        while (!ChainMath.validSolution(hash, header.getBits())) {
            header.setNonce(block.getNonce() + 1);
            hash            = header.getHashCode();
        }
    }
}
