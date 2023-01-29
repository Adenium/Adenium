package io.adenium.core.avm;

public class AdeniumVirtualMachine {
    private final byte methodCache[] = new byte[32_000_000];
    private final byte  memory[];
    private final Cache cache;
    private final Stack stack;

    public AdeniumVirtualMachine() {
        memory = new byte[512_000_000];
        cache = new Cache(33_554_432, 1024, 256);
        stack = new Stack(16_000_000);
    }


    public void memoryStore(int location, Object object) {
    }
    public Object memoryFetch(int location) {
        // check if object is cached already
        // if not, load and cache it
        return null;
    }
}
