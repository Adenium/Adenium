package io.adenium.core.avm;

public class Cache {
    public byte cache[];
    public long memoryRangeBegin = 0;
    public long memoryRangeEnd   = 0;

    public Cache(int size, int units, int cacheLineSize) {
        this.cache = new byte[size];
        this.memoryRangeBegin = 0;
        this.memoryRangeEnd = 0;
    }

    // fetch operations MUST always be less than or equal to the size of cache.
    public boolean inRange(int address, int amt) {
        return address >= memoryRangeBegin &&
                (address + amt) <= memoryRangeEnd;
    }

    public byte[] fetch(int address, int amt) {
        int begin = (int) (address - memoryRangeBegin);
        int end = begin + amt;

        return Arrays.clone(cache, begin, end);
    }
}
