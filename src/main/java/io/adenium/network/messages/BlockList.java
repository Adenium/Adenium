package io.adenium.network.messages;

import io.adenium.core.BlockIndex;
import io.adenium.core.Context;
import io.adenium.exceptions.WolkenException;
import io.adenium.serialization.SerializableI;
import io.adenium.network.Node;
import io.adenium.network.Server;
import io.adenium.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockList extends ResponseMessage {
    private Set<BlockIndex>      blocks;

    public BlockList(int version, Collection<BlockIndex> blocks, byte[] uniqueMessageIdentifier) {
        super(version, uniqueMessageIdentifier);
        this.blocks   = new LinkedHashSet<>(blocks);
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(blocks.size(), stream);
        for (BlockIndex block : blocks)
        {
            block.write(stream);
        }
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++)
        {
            try {
                BlockIndex block = Context.getInstance().getSerialFactory().fromStream(Context.getInstance().getSerialFactory().getSerialNumber(BlockIndex.class), stream);
                blocks.add(block);
            } catch (WolkenException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public <Type> Type getPayload() {
        return (Type) blocks;
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new BlockList(getVersion(), blocks, getUniqueMessageIdentifier());
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(BlockList.class);
    }

    @Override
    public void execute(Server server, Node node) {
    }
}
