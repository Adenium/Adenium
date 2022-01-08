package io.adenium.utils;

public class NetworkTimestamp {
    private final long myTime;
    private final  long peerTime;

    public NetworkTimestamp(long myTime, long peerTime) {
        this.myTime = myTime;
        this.peerTime = peerTime;
    }

    public long getDifference() {
        return myTime - peerTime;
    }
}
