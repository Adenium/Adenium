package org.wolkenproject.utils;

import java.util.*;

public class LinkedHashQueue<T> implements HashQueue<T> {
    private Map<ByteArray, Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;
    private long                    byteCount;

    public LinkedHashQueue() {
        queue       = new LinkedList<>();
        entryMap    = new HashMap<>();
    }

    @Override
    public boolean containsKey(ByteArray hash) {
        return entryMap.containsKey(hash);
    }

    @Override
    public void removeTails(int newLength, VoidCallableTY<T, byte[]> callable) {
        while (!isEmpty() && size() > newLength) {
            Entry<T> entry = queue.peek();
            poll();

            callable.call(entry.element, entry.hash);
        }
    }

    @Override
    public T getByHash(ByteArray hash) {
        if (entryMap.containsKey(hash)) {
            return entryMap.get(hash).element;
        }

        return null;
    }

    @Override
    public void add(T element, byte[] hash) {
        Entry<T> entry  = new Entry<>();
        entry.element   = element;
        entry.hash      = hash;

        entryMap.put(ByteArray.wrap(hash), entry);
        queue.add(entry);
    }

    @Override
    public T poll() {
        if (queue.isEmpty()) {
            return null;
        }

        Entry<T> entry = queue.poll();
        entryMap.remove(ByteArray.wrap(entry.hash));

        return entry.element;
    }

    @Override
    public T peek() {
        if (queue.isEmpty()) {
            return null;
        }

        return queue.peek().element;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public long byteCount() {
        return byteCount;
    }

    private static class Entry<T> {
        private T       element;
        private byte[]  hash;
    }
}
